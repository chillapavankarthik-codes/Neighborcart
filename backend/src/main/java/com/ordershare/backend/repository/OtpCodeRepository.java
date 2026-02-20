package com.ordershare.backend.repository;

import com.ordershare.backend.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findTopByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
