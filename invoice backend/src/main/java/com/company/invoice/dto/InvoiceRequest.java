package com.company.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record InvoiceRequest(
        @NotNull Long customerId,
        LocalDate issueDate,
        LocalDate dueDate,
        String placeOfSupply,
        String notes,
        String terms,
        @NotEmpty @Valid List<InvoiceItemRequest> items) {
}
