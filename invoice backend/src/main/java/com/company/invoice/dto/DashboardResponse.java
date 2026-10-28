package com.company.invoice.dto;

import com.company.invoice.entity.Invoice;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        DashboardStatsResponse stats,
        List<MonthlyRevenue> monthlyRevenue,
        Map<String, Long> statusCounts,
        List<InvoiceRow> recentInvoices,
        List<InvoiceRow> dueSoon) {

    public record MonthlyRevenue(String month, BigDecimal amount) {}

    public record InvoiceRow(Long id, String invoiceNumber, String customerName,
                             BigDecimal total, BigDecimal balance,
                             LocalDate dueDate, Invoice.Status status) {
        public static InvoiceRow from(Invoice i) {
            return new InvoiceRow(i.getId(), i.getInvoiceNumber(), i.getCustomer().getName(),
                    i.getTotal(), i.getTotal().subtract(i.getAmountPaid()),
                    i.getDueDate(), i.getStatus());
        }
    }
}