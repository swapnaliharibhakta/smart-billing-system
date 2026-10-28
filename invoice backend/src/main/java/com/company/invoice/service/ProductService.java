package com.company.invoice.service;

import com.company.invoice.dto.ProductRequest;
import com.company.invoice.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    Page<ProductResponse> getAll(String q, Pageable pageable);

    ProductResponse getById(Long id);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
}