package com.company.invoice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerRequest(
        @NotBlank(message = "Name is required") String name,
        @Email(message = "Invalid email") String email,
        @Pattern(regexp = "^$|^[6-9]\\d{9}$", message = "Phone must be a valid 10-digit number") String phone,
        String address,
        @Pattern(regexp = "^$|^\\d{2}[A-Z]{5}\\d{4}[A-Z][1-9A-Z]Z[0-9A-Z]$",
                 message = "Invalid GSTIN format") String gstNumber,
        // Billing address
        String billingStreet,
        String billingCity,
        String billingState,
        @Pattern(regexp = "^$|^\\d{2}$", message = "State code must be 2 digits") String billingStateCode,
        @Pattern(regexp = "^$|^\\d{6}$", message = "Pincode must be 6 digits") String billingPincode,
        // Shipping address
        String shippingName,
        String shippingStreet,
        String shippingCity,
        String shippingState,
        @Pattern(regexp = "^$|^\\d{2}$", message = "State code must be 2 digits") String shippingStateCode,
        @Pattern(regexp = "^$|^\\d{6}$", message = "Pincode must be 6 digits") String shippingPincode) {
}
