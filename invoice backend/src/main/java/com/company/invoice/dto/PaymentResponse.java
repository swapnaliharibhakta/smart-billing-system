package com.company.invoice.dto;

import com.company.invoice.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long invoiceId,
        BigDecimal amount,
        Payment.Method method,
        LocalDate paidOn,
        String referenceNo,
        String notes,
        LocalDateTime createdAt) {

    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getInvoice().getId(),
                p.getAmount(),
                p.getMethod(),
                p.getPaidOn(),
                p.getReferenceNo(),
                p.getNotes(),
                p.getCreatedAt());
    }
}