package com.company.invoice.repository;

import com.company.invoice.entity.Invoice;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByInvoiceNumber(String invoiceNumber);

    @Query("""
           select i from Invoice i join i.customer c
           where lower(i.invoiceNumber) like lower(concat('%', :q, '%'))
              or lower(c.name) like lower(concat('%', :q, '%'))
           """)
    Page<Invoice> search(@Param("q") String q, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Invoice i where i.id = :id")
    Optional<Invoice> findByIdForUpdate(@Param("id") Long id);

    // ---- Dashboard ----
    @Query("select coalesce(sum(i.total), 0) from Invoice i where i.status in :statuses")
    BigDecimal sumTotalByStatusIn(@Param("statuses") Collection<Invoice.Status> statuses);

    @Query("select coalesce(sum(i.amountPaid), 0) from Invoice i where i.status in :statuses")
    BigDecimal sumPaidByStatusIn(@Param("statuses") Collection<Invoice.Status> statuses);

    @Query("select coalesce(sum(i.total - i.amountPaid), 0) from Invoice i "
            + "where i.status in :statuses and i.dueDate < :today")
    BigDecimal sumOverdueBalance(@Param("statuses") Collection<Invoice.Status> statuses,
                                 @Param("today") LocalDate today);

    @Query("select count(i) from Invoice i where i.status in :statuses and i.dueDate < :today")
    long countOverdue(@Param("statuses") Collection<Invoice.Status> statuses,
                      @Param("today") LocalDate today);

    long countByStatusIn(Collection<Invoice.Status> statuses);

    @Query("select i.status, count(i) from Invoice i group by i.status")
    List<Object[]> countGroupedByStatus();

    List<Invoice> findTop5ByOrderByCreatedAtDesc();

    List<Invoice> findTop5ByStatusInAndDueDateLessThanEqualOrderByDueDateAsc(
            Collection<Invoice.Status> statuses, LocalDate dueBy);

    // ---- Reports ----
    @Query("select coalesce(sum(i.total), 0) from Invoice i where i.issueDate between :from and :to and i.status in :statuses")
    BigDecimal sumTotalByDateRangeAndStatus(@Param("from") LocalDate from, @Param("to") LocalDate to,
                                            @Param("statuses") Collection<Invoice.Status> statuses);

    @Query("select coalesce(sum(i.amountPaid), 0) from Invoice i where i.issueDate between :from and :to and i.status in :statuses")
    BigDecimal sumPaidByDateRangeAndStatus(@Param("from") LocalDate from, @Param("to") LocalDate to,
                                           @Param("statuses") Collection<Invoice.Status> statuses);

    @Query("select coalesce(sum(i.total - i.amountPaid), 0) from Invoice i where i.issueDate between :from and :to and i.status in :open and i.dueDate < :today")
    BigDecimal sumOverdueByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to,
                                     @Param("open") Collection<Invoice.Status> open, @Param("today") LocalDate today);

    @Query("select count(i) from Invoice i where i.issueDate between :from and :to")
    long countByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select i.status, count(i) from Invoice i where i.issueDate between :from and :to group by i.status")
    List<Object[]> countGroupedByStatusInRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select c.name, count(i), sum(i.total), sum(i.amountPaid) "
            + "from Invoice i join i.customer c "
            + "where i.issueDate between :from and :to "
            + "group by c.id, c.name "
            + "order by sum(i.total) desc "
            + "limit 5")
    List<Object[]> topCustomers(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select coalesce(sum(ii.lineTotal), 0), coalesce(sum(ii.lineTotal * ii.taxRate / 100), 0) "
            + "from InvoiceItem ii join ii.invoice i "
            + "where i.issueDate between :from and :to and i.status not in :excluded")
    List<Object[]> gstSummary(@Param("from") LocalDate from, @Param("to") LocalDate to,
                              @Param("excluded") Collection<Invoice.Status> excluded);
}