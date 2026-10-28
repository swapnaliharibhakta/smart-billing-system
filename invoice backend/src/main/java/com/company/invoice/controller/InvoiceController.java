package com.company.invoice.controller;

import com.company.invoice.dto.InvoiceRequest;
import com.company.invoice.dto.InvoiceResponse;
import com.company.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.create(request, authentication.getName()));
    }

    @GetMapping
    public Page<InvoiceResponse> getAll(@RequestParam(required = false) String q,
                                        @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return invoiceService.getAll(q, pageable);
    }

    @GetMapping("/{id}")
    public InvoiceResponse getById(@PathVariable Long id) {
        return invoiceService.getById(id);
    }

    @PutMapping("/{id}")
    public InvoiceResponse update(@PathVariable Long id, @Valid @RequestBody InvoiceRequest request) {
        return invoiceService.update(id, request);
    }

    @PatchMapping("/{id}/send")
    public InvoiceResponse markAsSent(@PathVariable Long id) {
        return invoiceService.markAsSent(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        invoiceService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}