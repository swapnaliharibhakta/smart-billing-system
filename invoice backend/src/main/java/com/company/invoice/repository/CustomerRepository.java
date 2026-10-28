package com.company.invoice.repository;

import com.company.invoice.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    @Query("""
            SELECT c FROM Customer c
            WHERE c.active = true
              AND (:q IS NULL OR :q = ''
                   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(c.email) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR c.phone LIKE CONCAT('%', :q, '%'))
            """)
    Page<Customer> search(@Param("q") String q, Pageable pageable);
}