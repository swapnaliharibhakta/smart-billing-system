package com.company.invoice.controller;

import com.company.invoice.dto.*;
import com.company.invoice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) { this.service = service; }

    @PostMapping("/invoices/{invoiceId}/payments")
    public ResponseEntity<PaymentResponse> add(@PathVariable Long invoiceId,
                                               @Valid @RequestBody PaymentRequest req,
                                               Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addPayment(invoiceId, req, auth.getName()));
    }

    @GetMapping("/invoices/{invoiceId}/payments")
    public List<PaymentResponse> list(@PathVariable Long invoiceId) {
        return service.list(invoiceId);
    }

    @DeleteMapping("/payments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}