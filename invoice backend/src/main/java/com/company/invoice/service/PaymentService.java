package com.company.invoice.service;

import com.company.invoice.dto.PaymentRequest;
import com.company.invoice.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse addPayment(Long invoiceId, PaymentRequest req, String userEmail);

    List<PaymentResponse> list(Long invoiceId);

    void delete(Long paymentId);
}