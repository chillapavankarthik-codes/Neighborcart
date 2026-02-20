package com.ordershare.backend.auth;

import com.ordershare.backend.dto.AuthResponse;
import com.ordershare.backend.dto.AuthUserResponse;
import com.ordershare.backend.dto.RequestOtpResponse;
import com.ordershare.backend.dto.VerifyOtpRequest;
import com.ordershare.backend.entity.AppUser;
import com.ordershare.backend.entity.AuthSession;
import com.ordershare.backend.entity.OtpCode;
import com.ordershare.backend.repository.AppUserRepository;
import com.ordershare.backend.repository.AuthSessionRepository;
import com.ordershare.backend.repository.OtpCodeRepository;
import com.ordershare.backend.security.AuthenticatedUser;
import com.ordershare.backend.util.CryptoUtil;
import com.ordershare.backend.util.PhoneNumberNormalizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final OtpCodeRepository otpCodeRepository;
    private final AppUserRepository appUserRepository;
    private final AuthSessionRepository authSessionRepository;
    private final PhoneNumberNormalizer phoneNumberNormalizer;
    private final CryptoUtil cryptoUtil;

    private final int otpExpiryMinutes;
    private final int sessionExpiryHours;
    private final boolean devOtpEnabled;

    public AuthService(
            OtpCodeRepository otpCodeRepository,
            AppUserRepository appUserRepository,
            AuthSessionRepository authSessionRepository,
            PhoneNumberNormalizer phoneNumberNormalizer,
            CryptoUtil cryptoUtil,
            @Value("${app.auth.otp-expiry-minutes:5}") int otpExpiryMinutes,
            @Value("${app.auth.session-expiry-hours:168}") int sessionExpiryHours,
            @Value("${app.auth.dev-otp-enabled:true}") boolean devOtpEnabled
    ) {
        this.otpCodeRepository = otpCodeRepository;
        this.appUserRepository = appUserRepository;
        this.authSessionRepository = authSessionRepository;
        this.phoneNumberNormalizer = phoneNumberNormalizer;
        this.cryptoUtil = cryptoUtil;
        this.otpExpiryMinutes = otpExpiryMinutes;
        this.sessionExpiryHours = sessionExpiryHours;
        this.devOtpEnabled = devOtpEnabled;
    }

    public RequestOtpResponse requestOtp(String rawPhoneNumber) {
        String normalizedPhone = phoneNumberNormalizer.normalizeUsNumber(rawPhoneNumber);
        otpCodeRepository.findTopByPhoneNumberOrderByCreatedAtDesc(normalizedPhone)
                .ifPresent(lastOtp -> {
                    if (lastOtp.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(45))) {
                        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Please wait before requesting another OTP");
                    }
                });

        String otpCode = cryptoUtil.randomOtpCode();
        String otpHash = cryptoUtil.sha256(otpCode);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpiryMinutes);

        OtpCode otp = new OtpCode(normalizedPhone, otpHash, expiresAt);
        otpCodeRepository.save(otp);

        return new RequestOtpResponse(
                "OTP generated. Use the code to verify your phone.",
                expiresAt,
                devOtpEnabled ? otpCode : null
        );
    }

    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String normalizedPhone = phoneNumberNormalizer.normalizeUsNumber(request.getPhoneNumber());
        OtpCode otpCode = otpCodeRepository.findTopByPhoneNumberOrderByCreatedAtDesc(normalizedPhone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No OTP found for this phone"));

        if (otpCode.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP was already used");
        }
        if (otpCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        String providedHash = cryptoUtil.sha256(request.getOtpCode());
        if (!providedHash.equals(otpCode.getCodeHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP code");
        }

        otpCode.markUsed();
        otpCodeRepository.save(otpCode);

        AppUser user = appUserRepository.findByPhoneNumber(normalizedPhone)
                .map(existing -> {
                    existing.updateDisplayName(request.getDisplayName());
                    return appUserRepository.save(existing);
                })
                .orElseGet(() -> appUserRepository.save(new AppUser(request.getDisplayName(), normalizedPhone)));

        authSessionRepository.findByUser_IdAndRevokedFalse(user.getId())
                .forEach(session -> {
                    session.revoke();
                    authSessionRepository.save(session);
                });

        String accessToken = cryptoUtil.randomAccessToken();
        String tokenHash = cryptoUtil.sha256(accessToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(sessionExpiryHours);

        AuthSession session = new AuthSession(user, tokenHash, expiresAt);
        authSessionRepository.save(session);

        return new AuthResponse(
                accessToken,
                "Bearer",
                expiresAt,
                new AuthUserResponse(user.getId(), user.getDisplayName(), user.getPhoneNumber())
        );
    }

    public Optional<AuthenticatedUser> authenticate(String rawToken) {
        String tokenHash = cryptoUtil.sha256(rawToken);

        return authSessionRepository.findByTokenHashAndRevokedFalseAndExpiresAtAfter(tokenHash, LocalDateTime.now())
                .map(AuthSession::getUser)
                .map(user -> new AuthenticatedUser(user.getId(), user.getDisplayName(), user.getPhoneNumber()));
    }

    public AuthUserResponse getCurrentUser(AuthenticatedUser user) {
        return new AuthUserResponse(user.userId(), user.displayName(), user.phoneNumber());
    }

    public void logout(AuthenticatedUser user, String rawToken) {
        String tokenHash = cryptoUtil.sha256(rawToken);
        AuthSession session = authSessionRepository.findByTokenHashAndRevokedFalseAndExpiresAtAfter(tokenHash, LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        if (!session.getUser().getId().equals(user.userId())) {
            throw new AccessDeniedException("You cannot revoke another user's session");
        }

        session.revoke();
        authSessionRepository.save(session);
    }
}
