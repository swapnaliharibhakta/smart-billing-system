package com.company.invoice.repository;

import com.company.invoice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
              AND (:q IS NULL OR :q = ''
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<Product> search(@Param("q") String q, Pageable pageable);
}