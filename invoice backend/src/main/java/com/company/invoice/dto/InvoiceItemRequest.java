package com.company.invoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InvoiceItemRequest(
        Long productId,
        @NotBlank String description,
        String hsnSac,
        String unit,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal quantity,
        @NotNull @DecimalMin("0.0") BigDecimal unitPrice,
        @DecimalMin("0.0") BigDecimal taxRate,
        @DecimalMin("0.0") BigDecimal discountPct) {
}
