package com.company.invoice.service;

import com.company.invoice.dto.CustomerRequest;
import com.company.invoice.dto.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponse create(CustomerRequest request);

    Page<CustomerResponse> getAll(String q, Pageable pageable);

    CustomerResponse getById(Long id);

    CustomerResponse update(Long id, CustomerRequest request);

    void delete(Long id);
}