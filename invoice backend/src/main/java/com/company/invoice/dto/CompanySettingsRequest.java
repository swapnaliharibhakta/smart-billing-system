package com.company.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CompanySettingsRequest(
        @NotBlank String companyName,
        String address,
        @Pattern(regexp = "^$|^\\d{2}[A-Z]{5}\\d{4}[A-Z][1-9A-Z]Z[0-9A-Z]$",
                 message = "Invalid GSTIN format") String gstNumber,
        String email,
        String phone,
        String invoicePrefix,
        Integer defaultDueDays,
        @Pattern(regexp = "^$|^[A-Z]{5}\\d{4}[A-Z]$",
                 message = "Invalid PAN format") String pan,
        @Pattern(regexp = "^$|^\\d{2}$",
                 message = "State code must be 2 digits") String stateCode,
        String bankName,
        String bankAccount,
        @Pattern(regexp = "^$|^[A-Z]{4}0[A-Z0-9]{6}$",
                 message = "Invalid IFSC format") String bankIfsc,
        String bankBranch,
        String upiId,
        String defaultTerms,
        String defaultNotes) {
}
