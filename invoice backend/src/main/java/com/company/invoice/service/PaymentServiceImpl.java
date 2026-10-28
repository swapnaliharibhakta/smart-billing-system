package com.company.invoice.service;

import com.company.invoice.dto.PaymentRequest;
import com.company.invoice.dto.PaymentResponse;
import com.company.invoice.entity.Invoice;
import com.company.invoice.entity.Payment;
import com.company.invoice.repository.InvoiceRepository;
import com.company.invoice.repository.PaymentRepository;
import com.company.invoice.repository.UserRepository;
import com.company.invoice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Set<Invoice.Status> PAYABLE =
            Set.of(Invoice.Status.SENT, Invoice.Status.PARTIALLY_PAID, Invoice.Status.OVERDUE);

    private final PaymentRepository paymentRepo;
    private final InvoiceRepository invoiceRepo;
    private final UserRepository userRepo;

    public PaymentServiceImpl(PaymentRepository paymentRepo,
                              InvoiceRepository invoiceRepo,
                              UserRepository userRepo) {
        this.paymentRepo = paymentRepo;
        this.invoiceRepo = invoiceRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public PaymentResponse addPayment(Long invoiceId, PaymentRequest req, String userEmail) {
        Invoice invoice = invoiceRepo.findByIdForUpdate(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        if (!PAYABLE.contains(invoice.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Payment allowed only for SENT, PARTIALLY_PAID or OVERDUE invoices");
        }

        BigDecimal balance = invoice.getTotal().subtract(invoice.getAmountPaid());
        if (req.amount().compareTo(balance) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Amount exceeds balance (" + balance + ")");
        }

        Payment p = new Payment();
        p.setInvoice(invoice);
        p.setAmount(req.amount());
        p.setMethod(req.method());
        p.setPaidOn(req.paidOn() != null ? req.paidOn() : LocalDate.now());
        p.setReferenceNo(req.referenceNo());
        p.setNotes(req.notes());
        p.setCreatedBy(userRepo.findByEmail(userEmail).orElse(null));
        paymentRepo.save(p);

        recalculate(invoice);
        return PaymentResponse.from(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> list(Long invoiceId) {
        return paymentRepo.findByInvoiceIdOrderByPaidOnDescIdDesc(invoiceId)
                .stream().map(PaymentResponse::from).toList();
    }

    @Override
    @Transactional
    public void delete(Long paymentId) {
        Payment p = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        Invoice invoice = invoiceRepo.findByIdForUpdate(p.getInvoice().getId()).orElseThrow();
        paymentRepo.delete(p);
        paymentRepo.flush();
        recalculate(invoice);
    }

    private void recalculate(Invoice invoice) {
        BigDecimal paid = paymentRepo.sumByInvoiceId(invoice.getId());
        invoice.setAmountPaid(paid);
        if (paid.compareTo(invoice.getTotal()) >= 0) {
            invoice.setStatus(Invoice.Status.PAID);
        } else if (paid.signum() > 0) {
            invoice.setStatus(Invoice.Status.PARTIALLY_PAID);
        } else {
            invoice.setStatus(invoice.getDueDate().isBefore(LocalDate.now())
                    ? Invoice.Status.OVERDUE : Invoice.Status.SENT);
        }
        invoiceRepo.save(invoice);
    }
}