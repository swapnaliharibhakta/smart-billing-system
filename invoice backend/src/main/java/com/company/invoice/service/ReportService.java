package com.company.invoice.service;

import com.company.invoice.dto.ReportResponse;

import java.time.LocalDate;

public interface ReportService {
    ReportResponse getSummary(LocalDate from, LocalDate to);
}
