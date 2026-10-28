package com.company.invoice.service;

import com.company.invoice.dto.InvoiceRequest;
import com.company.invoice.dto.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {

    InvoiceResponse create(InvoiceRequest request, String userEmail);

    Page<InvoiceResponse> getAll(String q, Pageable pageable);

    InvoiceResponse getById(Long id);

    InvoiceResponse update(Long id, InvoiceRequest request);

    InvoiceResponse markAsSent(Long id);

    void cancel(Long id);
}