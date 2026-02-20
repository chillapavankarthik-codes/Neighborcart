package com.ordershare.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_posts")
public class OrderPost {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private AppUser ownerUser;

    @Column(nullable = false, length = 80)
    private String storeName;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false, length = 100)
    private String addressHint;

    @Column(nullable = false)
    private LocalDateTime expectedDeliveryTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentCartAmount;

    @Column(nullable = false)
    private int postRadiusMiles;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 300)
    private String notes;

    @Column(nullable = false, length = 20)
    private String maskedPhone;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean phoneRevealEnabled;

    @Column(nullable = false)
    private int interestedCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected OrderPost() {
    }

    public OrderPost(
            AppUser ownerUser,
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
        this.ownerUser = ownerUser;
        this.storeName = storeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressHint = addressHint;
        this.expectedDeliveryTime = expectedDeliveryTime;
        this.minimumOrderAmount = minimumOrderAmount;
        this.currentCartAmount = currentCartAmount;
        this.postRadiusMiles = postRadiusMiles;
        this.title = title;
        this.notes = notes;
        this.maskedPhone = maskedPhone;
        this.phoneNumber = phoneNumber;
        this.phoneRevealEnabled = false;
        this.interestedCount = 0;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    public BigDecimal remainingAmount() {
        BigDecimal remaining = minimumOrderAmount.subtract(currentCartAmount);
        if (remaining.signum() < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return remaining.setScale(2, RoundingMode.HALF_UP);
    }

    public void registerInterest() {
        interestedCount += 1;
        updatedAt = LocalDateTime.now();
    }

    public void setPhoneRevealEnabled(boolean phoneRevealEnabled) {
        this.phoneRevealEnabled = phoneRevealEnabled;
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public AppUser getOwnerUser() {
        return ownerUser;
    }

    public String getStoreName() {
        return storeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddressHint() {
        return addressHint;
    }

    public LocalDateTime getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public BigDecimal getCurrentCartAmount() {
        return currentCartAmount;
    }

    public int getPostRadiusMiles() {
        return postRadiusMiles;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public String getMaskedPhone() {
        return maskedPhone;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isPhoneRevealEnabled() {
        return phoneRevealEnabled;
    }

    public int getInterestedCount() {
        return interestedCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
