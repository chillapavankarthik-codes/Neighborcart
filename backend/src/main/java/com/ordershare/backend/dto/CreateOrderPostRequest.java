package com.ordershare.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateOrderPostRequest {

    @Size(max = 40)
    private String initiatorAlias;

    @NotBlank
    @Size(max = 80)
    private String storeName;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotBlank
    @Size(max = 100)
    private String addressHint;

    @NotNull
    private LocalDateTime expectedDeliveryTime;

    @NotNull
    @DecimalMin(value = "1.0")
    private BigDecimal minimumOrderAmount;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal currentCartAmount;

    @Min(1)
    @Max(10)
    private int postRadiusMiles;

    @NotBlank
    @Size(max = 120)
    private String title;

    @Size(max = 300)
    private String notes;

    @NotBlank
    @Size(max = 20)
    private String maskedPhone;

    @Size(max = 20)
    private String phoneNumber;

    public String getInitiatorAlias() {
        return initiatorAlias;
    }

    public void setInitiatorAlias(String initiatorAlias) {
        this.initiatorAlias = initiatorAlias;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddressHint() {
        return addressHint;
    }

    public void setAddressHint(String addressHint) {
        this.addressHint = addressHint;
    }

    public LocalDateTime getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(LocalDateTime expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(BigDecimal minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public BigDecimal getCurrentCartAmount() {
        return currentCartAmount;
    }

    public void setCurrentCartAmount(BigDecimal currentCartAmount) {
        this.currentCartAmount = currentCartAmount;
    }

    public int getPostRadiusMiles() {
        return postRadiusMiles;
    }

    public void setPostRadiusMiles(int postRadiusMiles) {
        this.postRadiusMiles = postRadiusMiles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMaskedPhone() {
        return maskedPhone;
    }

    public void setMaskedPhone(String maskedPhone) {
        this.maskedPhone = maskedPhone;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
