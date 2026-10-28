package com.company.invoice.dto;

import java.math.BigDecimal;

public record DashboardStatsResponse(
        BigDecimal totalInvoiced, BigDecimal totalReceived,
        BigDecimal pendingAmount, BigDecimal overdueAmount,
        BigDecimal receivedThisMonth,
        long totalInvoices, long paidCount, long pendingCount, long overdueCount) {

}