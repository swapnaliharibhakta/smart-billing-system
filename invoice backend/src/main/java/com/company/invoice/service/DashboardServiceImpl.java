package com.company.invoice.service;

import com.company.invoice.dto.DashboardResponse;
import com.company.invoice.dto.DashboardResponse.InvoiceRow;
import com.company.invoice.dto.DashboardResponse.MonthlyRevenue;
import com.company.invoice.dto.DashboardStatsResponse;
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
public class DashboardServiceImpl implements DashboardService {

    private static final Set<Invoice.Status> BILLED =
            EnumSet.of(Invoice.Status.SENT, Invoice.Status.PARTIALLY_PAID,
                    Invoice.Status.PAID, Invoice.Status.OVERDUE);
    private static final Set<Invoice.Status> OPEN =
            EnumSet.of(Invoice.Status.SENT, Invoice.Status.PARTIALLY_PAID, Invoice.Status.OVERDUE);

    private final InvoiceRepository invoiceRepo;
    private final PaymentRepository paymentRepo;

    public DashboardServiceImpl(InvoiceRepository invoiceRepo, PaymentRepository paymentRepo) {
        this.invoiceRepo = invoiceRepo;
        this.paymentRepo = paymentRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getSummary() {
        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);

        BigDecimal invoiced = invoiceRepo.sumTotalByStatusIn(BILLED);
        BigDecimal received = invoiceRepo.sumPaidByStatusIn(BILLED);
        BigDecimal pending = invoiced.subtract(received);
        BigDecimal overdueAmt = invoiceRepo.sumOverdueBalance(OPEN, today);
        long overdueCnt = invoiceRepo.countOverdue(OPEN, today);
        BigDecimal receivedThisMonth =
                paymentRepo.sumBetween(thisMonth.atDay(1), thisMonth.atEndOfMonth());

        long totalInvoices = invoiceRepo.count();
        long paidCount = invoiceRepo.countByStatusIn(EnumSet.of(Invoice.Status.PAID));
        long pendingCount = invoiceRepo.countByStatusIn(OPEN);

        DashboardStatsResponse stats = new DashboardStatsResponse(
                invoiced, received, pending, overdueAmt, receivedThisMonth,
                totalInvoices, paidCount, pendingCount, overdueCnt);

        // status counts (all statuses, zero if none)
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (Invoice.Status s : Invoice.Status.values()) statusCounts.put(s.name(), 0L);
        for (Object[] row : invoiceRepo.countGroupedByStatus()) {
            statusCounts.put(((Invoice.Status) row[0]).name(), ((Number) row[1]).longValue());
        }

        // last 6 months revenue (fill missing months with 0)
        YearMonth start = thisMonth.minusMonths(5);
        Map<YearMonth, BigDecimal> byMonth = new HashMap<>();
        for (Object[] row : paymentRepo.monthlyTotals(start.atDay(1))) {
            byMonth.put(YearMonth.of(((Number) row[0]).intValue(), ((Number) row[1]).intValue()),
                    (BigDecimal) row[2]);
        }
        DateTimeFormatter label = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
        List<MonthlyRevenue> monthly = new ArrayList<>();
        for (int k = 0; k < 6; k++) {
            YearMonth ym = start.plusMonths(k);
            monthly.add(new MonthlyRevenue(ym.format(label), byMonth.getOrDefault(ym, BigDecimal.ZERO)));
        }

        List<InvoiceRow> recent = invoiceRepo.findTop5ByOrderByCreatedAtDesc()
                .stream().map(InvoiceRow::from).toList();
        List<InvoiceRow> dueSoon = invoiceRepo
                .findTop5ByStatusInAndDueDateLessThanEqualOrderByDueDateAsc(OPEN, today.plusDays(7))
                .stream().map(InvoiceRow::from).toList();

        return new DashboardResponse(stats, monthly, statusCounts, recent, dueSoon);
    }
}