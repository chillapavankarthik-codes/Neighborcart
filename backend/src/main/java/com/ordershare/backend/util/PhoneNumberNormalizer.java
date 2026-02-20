package com.ordershare.backend.util;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberNormalizer {

    public String normalizeUsNumber(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        String digits = input.replaceAll("\\D", "");
        if (digits.length() == 10) {
            return "+1" + digits;
        }
        if (digits.length() == 11 && digits.startsWith("1")) {
            return "+" + digits;
        }

        throw new IllegalArgumentException("Phone number must be a valid US number");
    }
}
