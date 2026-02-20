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
        if (orderPostRepository.count() > 0) {
            return;
        }

        AppUser ari = appUserRepository.findByPhoneNumber("+14155551284")
                .orElseGet(() -> appUserRepository.save(new AppUser("Ari", "+14155551284")));
        AppUser nia = appUserRepository.findByPhoneNumber("+14155559988")
                .orElseGet(() -> appUserRepository.save(new AppUser("Nia", "+14155559988")));
        AppUser milo = appUserRepository.findByPhoneNumber("+14155551102")
                .orElseGet(() -> appUserRepository.save(new AppUser("Milo", "+14155551102")));

        orderPostRepository.save(new OrderPost(
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
        ));

        orderPostRepository.save(new OrderPost(
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
        ));

        orderPostRepository.save(new OrderPost(
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
        ));
    }
}
