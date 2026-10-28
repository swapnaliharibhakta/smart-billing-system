package com.company.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceResponse(
        // --- existing fields (unchanged) ---
        Long id,
        String invoiceNumber,
        Long customerId,
        String customerName,
        LocalDate issueDate,
        LocalDate dueDate,
        String status,
        BigDecimal subtotal,
        BigDecimal taxTotal,
        BigDecimal total,
        BigDecimal amountPaid,
        BigDecimal balanceDue,
        String notes,
        LocalDateTime sentAt,
        List<InvoiceItemResponse> items,
        LocalDateTime createdAt,
        // --- new fields ---
        String placeOfSupply,
        String terms,
        BigDecimal roundOff,
        String amountInWords,
        // bill-to snapshot
        String billToName,
        String billToGstin,
        String billToStreet,
        String billToCity,
        String billToState,
        String billToStateCode,
        // ship-to snapshot
        String shipToName,
        String shipToStreet,
        String shipToCity,
        String shipToState,
        String shipToStateCode) {
}
