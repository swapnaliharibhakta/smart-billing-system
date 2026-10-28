package com.company.invoice.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String hsnSac,
        String unit,
        BigDecimal unitPrice,
        BigDecimal taxRate,
        boolean active) {
}
