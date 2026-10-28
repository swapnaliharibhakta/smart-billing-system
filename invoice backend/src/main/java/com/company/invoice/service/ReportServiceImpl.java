package com.company.invoice.service;

import com.company.invoice.dto.ReportResponse;
import com.company.invoice.dto.ReportResponse.*;
import com.company.invoice.entity.Invoice;
import com.company.invoice.repository.InvoiceRepository;
import com.company.invoice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Set<Invoice.Status> BILLED =
            EnumSet.of(Invoice.Status.SENT, Invoice.Status.PARTIALLY_PAID,
                    Invoice.Status.PAID, Invoice.Status.OVERDUE);
    private static final Set<Invoice.Status> OPEN =
            EnumSet.of(Invoice.Status.SENT, Invoice.Status.PARTIALLY_PAID, Invoice.Status.OVERDUE);
    private static final Set<Invoice.Status> EXCLUDED_FROM_GST =
            EnumSet.of(Invoice.Status.DRAFT, Invoice.Status.CANCELLED);

    private final InvoiceRepository invoiceRepo;
    private final PaymentRepository paymentRepo;

    public ReportServiceImpl(InvoiceRepository invoiceRepo, PaymentRepository paymentRepo) {
        this.invoiceRepo = invoiceRepo;
        this.paymentRepo = paymentRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse getSummary(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();

        BigDecimal invoiced = invoiceRepo.sumTotalByDateRangeAndStatus(from, to, BILLED);
        BigDecimal paid = invoiceRepo.sumPaidByDateRangeAndStatus(from, to, BILLED);
        BigDecimal pending = invoiced.subtract(paid);
        BigDecimal overdue = invoiceRepo.sumOverdueByDateRange(from, to, OPEN, today);
        long count = invoiceRepo.countByDateRange(from, to);

        ReportStats stats = new ReportStats(invoiced, paid, pending, overdue, count);

        // monthly revenue within range
        Map<YearMonth, BigDecimal> byMonth = new HashMap<>();
        for (Object[] row : paymentRepo.monthlyTotalsInRange(from, to)) {
            byMonth.put(YearMonth.of(((Number) row[0]).intValue(), ((Number) row[1]).intValue()),
                    (BigDecimal) row[2]);
        }
        DateTimeFormatter label = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
        List<MonthlyRevenue> monthly = new ArrayList<>();
        YearMonth ymFrom = YearMonth.from(from);
        YearMonth ymTo = YearMonth.from(to);
        for (YearMonth ym = ymFrom; !ym.isAfter(ymTo); ym = ym.plusMonths(1)) {
            monthly.add(new MonthlyRevenue(ym.format(label), byMonth.getOrDefault(ym, BigDecimal.ZERO)));
        }

        // status counts
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (Invoice.Status s : Invoice.Status.values()) statusCounts.put(s.name(), 0L);
        for (Object[] row : invoiceRepo.countGroupedByStatusInRange(from, to)) {
            statusCounts.put(((Invoice.Status) row[0]).name(), ((Number) row[1]).longValue());
        }

        // top 5 customers
        List<TopCustomer> topCustomers = new ArrayList<>();
        for (Object[] row : invoiceRepo.topCustomers(from, to)) {
            topCustomers.add(new TopCustomer(
                    (String) row[0],
                    ((Number) row[1]).longValue(),
                    (BigDecimal) row[2],
                    (BigDecimal) row[3]));
        }

        // GST summary
        List<Object[]> gst = invoiceRepo.gstSummary(from, to, EXCLUDED_FROM_GST);
        BigDecimal taxable = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        if (!gst.isEmpty() && gst.get(0)[0] != null) {
            taxable = (BigDecimal) gst.get(0)[0];
            totalTax = (BigDecimal) gst.get(0)[1];
        }
        GstSummary gstSummary = new GstSummary(taxable, totalTax);

        return new ReportResponse(stats, monthly, statusCounts, topCustomers, gstSummary);
    }
}
