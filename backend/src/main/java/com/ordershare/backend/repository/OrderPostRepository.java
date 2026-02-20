package com.ordershare.backend.repository;

import com.ordershare.backend.entity.OrderPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderPostRepository extends JpaRepository<OrderPost, String> {

    List<OrderPost> findAllByOrderByCreatedAtDesc();

    List<OrderPost> findAllByOwnerUser_IdOrderByCreatedAtDesc(String ownerUserId);

    boolean existsByOwnerUser_IdAndTitle(String ownerUserId, String title);
}
