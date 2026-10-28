package com.company.invoice.dto;

import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        String gstNumber,
        boolean active,
        LocalDateTime createdAt,
        // Billing address
        String billingStreet,
        String billingCity,
        String billingState,
        String billingStateCode,
        String billingPincode,
        // Shipping address
        String shippingName,
        String shippingStreet,
        String shippingCity,
        String shippingState,
        String shippingStateCode,
        String shippingPincode) {
}
