package com.ordershare.backend.config;

import com.ordershare.backend.entity.AppUser;
import com.ordershare.backend.entity.OrderPost;
import com.ordershare.backend.repository.AppUserRepository;
import com.ordershare.backend.repository.OrderPostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final OrderPostRepository orderPostRepository;

    public DataSeeder(AppUserRepository appUserRepository, OrderPostRepository orderPostRepository) {
        this.appUserRepository = appUserRepository;
        this.orderPostRepository = orderPostRepository;
    }

    @Override
    public void run(String... args) {
        AppUser ari = ensureUser("Ari", "+14155551284");
        AppUser nia = ensureUser("Nia", "+14155559988");
        AppUser milo = ensureUser("Milo", "+14155551102");
        AppUser ivy = ensureUser("Ivy", "+14155553321");
        AppUser noah = ensureUser("Noah", "+14155554467");
        AppUser zara = ensureUser("Zara", "+14155557734");

        ensureDemoPost(
                ari,
                "Walmart",
                37.7858,
                -122.401,
                "South Market St",
                LocalDateTime.now().plusHours(4),
                new BigDecimal("35.00"),
                new BigDecimal("29.50"),
                2,
                "Need $5.50 to unlock free delivery",
                "I am only adding dairy and eggs. Split delivery tips if needed.",
                "+1 (***) ***-1284",
                "+1-415-555-1284"
        );

        ensureDemoPost(
                nia,
                "Costco",
                37.781,
                -122.411,
                "Folsom & 3rd",
                LocalDateTime.now().plusHours(7),
                new BigDecimal("60.00"),
                new BigDecimal("47.00"),
                3,
                "Costco same-day order gap: $13",
                "Adding produce only. Order arrives around dinner time.",
                "+1 (***) ***-9988",
                "+1-415-555-9988"
        );

        ensureDemoPost(
                milo,
                "Instacart",
                37.778,
                -122.405,
                "Mission Bay",
                LocalDateTime.now().plusHours(2),
                new BigDecimal("30.00"),
                new BigDecimal("26.25"),
                1,
                "Almost there, just need $3.75",
                "Fast delivery slot. Can confirm your item instantly in chat.",
                "+1 (***) ***-1102",
                "+1-415-555-1102"
        );

        ensureDemoPost(
                ivy,
                "Sam's Club",
                37.7825,
                -122.4072,
                "Howard St pickup zone",
                LocalDateTime.now().plusHours(3),
                new BigDecimal("50.00"),
                new BigDecimal("42.00"),
                2,
                "Sam's Club order needs $8 more",
                "Looking for pantry items only. Can coordinate handoff near lobby.",
                "+1 (***) ***-3321",
                "+1-415-555-3321"
        );

        ensureDemoPost(
                noah,
                "Target",
                37.7801,
                -122.4029,
                "4th St & Mission",
                LocalDateTime.now().plusHours(5),
                new BigDecimal("35.00"),
                new BigDecimal("31.00"),
                2,
                "Need $4 to close Target order",
                "Quick essentials run. I will wait 10 mins before checkout.",
                "+1 (***) ***-4467",
                "+1-415-555-4467"
        );

        ensureDemoPost(
                zara,
                "Uber Eats",
                37.7794,
                -122.4098,
                "SoMa food pickup",
                LocalDateTime.now().plusHours(1),
                new BigDecimal("20.00"),
                new BigDecimal("16.50"),
                1,
                "Need $3.50 for free delivery",
                "Ordering from a local cafe. Add-ons welcome for next 15 minutes.",
                "+1 (***) ***-7734",
                "+1-415-555-7734"
        );
    }

    private AppUser ensureUser(String displayName, String phoneNumber) {
        return appUserRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> appUserRepository.save(new AppUser(displayName, phoneNumber)));
    }

    private void ensureDemoPost(
            AppUser owner,
            String storeName,
            double latitude,
            double longitude,
            String addressHint,
            LocalDateTime expectedDeliveryTime,
            BigDecimal minimumOrderAmount,
            BigDecimal currentCartAmount,
            int postRadiusMiles,
            String title,
            String notes,
            String maskedPhone,
            String phoneNumber
    ) {
        boolean exists = orderPostRepository.existsByOwnerUser_IdAndTitle(owner.getId(), title);
        if (exists) {
            return;
        }

        orderPostRepository.save(new OrderPost(
                owner,
                storeName,
                latitude,
                longitude,
                addressHint,
                expectedDeliveryTime,
                minimumOrderAmount,
                currentCartAmount,
                postRadiusMiles,
                title,
                notes,
                maskedPhone,
                phoneNumber
        ));
    }
}
