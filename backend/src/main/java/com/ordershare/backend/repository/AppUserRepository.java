package com.ordershare.backend.repository;

import com.ordershare.backend.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

    Optional<AppUser> findByPhoneNumber(String phoneNumber);
}
