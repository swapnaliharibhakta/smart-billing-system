package com.company.invoice.dto;

import java.math.BigDecimal;

public record InvoiceItemResponse(
        Long id,
        Long productId,
        String description,
        String hsnSac,
        String unit,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal discountPct,
        BigDecimal discountAmount,
        BigDecimal taxableValue,
        BigDecimal taxRate,
        BigDecimal cgstRate,
        BigDecimal cgstAmount,
        BigDecimal sgstRate,
        BigDecimal sgstAmount,
        BigDecimal igstRate,
        BigDecimal igstAmount,
        BigDecimal lineTotal) {
}
