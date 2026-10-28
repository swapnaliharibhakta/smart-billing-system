package com.company.invoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Name is required") String name,
        String description,
        String hsnSac,
        String unit,
        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0", message = "Unit price cannot be negative") BigDecimal unitPrice,
        @DecimalMin(value = "0.0", message = "Tax rate cannot be negative") BigDecimal taxRate) {
}
