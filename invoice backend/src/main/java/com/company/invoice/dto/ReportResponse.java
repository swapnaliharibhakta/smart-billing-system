package com.company.invoice.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ReportResponse(
        ReportStats stats,
        List<MonthlyRevenue> monthlyRevenue,
        Map<String, Long> statusCounts,
        List<TopCustomer> topCustomers,
        GstSummary gstSummary) {

    public record ReportStats(
            BigDecimal totalInvoiced,
            BigDecimal totalPaid,
            BigDecimal totalPending,
            BigDecimal totalOverdue,
            long invoiceCount) {}

    public record MonthlyRevenue(String month, BigDecimal amount) {}

    public record TopCustomer(String name, long invoiceCount, BigDecimal totalInvoiced, BigDecimal totalPaid) {}

    public record GstSummary(BigDecimal taxableAmount, BigDecimal totalTax) {}
}
