package com.company.invoice.repository;

import com.company.invoice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceIdOrderByPaidOnDescIdDesc(Long invoiceId);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.invoice.id = :invoiceId")
    BigDecimal sumByInvoiceId(@Param("invoiceId") Long invoiceId);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.paidOn between :from and :to")
    BigDecimal sumBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select year(p.paidOn), month(p.paidOn), sum(p.amount) from Payment p "
            + "where p.paidOn >= :from group by year(p.paidOn), month(p.paidOn)")
    List<Object[]> monthlyTotals(@Param("from") LocalDate from);

    @Query("select year(p.paidOn), month(p.paidOn), sum(p.amount) from Payment p "
            + "where p.paidOn between :from and :to group by year(p.paidOn), month(p.paidOn)")
    List<Object[]> monthlyTotalsInRange(@Param("from") LocalDate from, @Param("to") LocalDate to);
}