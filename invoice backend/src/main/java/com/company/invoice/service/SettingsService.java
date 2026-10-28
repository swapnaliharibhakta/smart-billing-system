package com.company.invoice.service;

import com.company.invoice.dto.CompanySettingsRequest;
import com.company.invoice.dto.CompanySettingsResponse;

public interface SettingsService {
    CompanySettingsResponse get();
    CompanySettingsResponse save(CompanySettingsRequest request);
}
