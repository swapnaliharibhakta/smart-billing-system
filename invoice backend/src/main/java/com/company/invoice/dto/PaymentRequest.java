package com.company.invoice.dto;

import com.company.invoice.entity.Payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull Payment.Method method,
        LocalDate paidOn,
        @Size(max = 100) String referenceNo,
        String notes) {}