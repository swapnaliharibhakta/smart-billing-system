package com.company.invoice.dto;

public record CompanySettingsResponse(
        Long id,
        String companyName,
        String address,
        String gstNumber,
        String email,
        String phone,
        String invoicePrefix,
        Long nextInvoiceNo,
        Integer defaultDueDays,
        String pan,
        String stateCode,
        String bankName,
        String bankAccount,
        String bankIfsc,
        String bankBranch,
        String upiId,
        String defaultTerms,
        String defaultNotes) {
}
