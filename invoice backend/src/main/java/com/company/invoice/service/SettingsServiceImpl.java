package com.company.invoice.service;

import com.company.invoice.dto.CompanySettingsRequest;
import com.company.invoice.dto.CompanySettingsResponse;
import com.company.invoice.entity.CompanySettings;
import com.company.invoice.repository.SettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository repo;

    public SettingsServiceImpl(SettingsRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public CompanySettingsResponse get() {
        return toResponse(getOrCreate());
    }

    @Override
    @Transactional
    public CompanySettingsResponse save(CompanySettingsRequest req) {
        CompanySettings s = getOrCreate();
        s.setCompanyName(req.companyName());
        s.setAddress(req.address());
        s.setGstNumber(req.gstNumber());
        s.setEmail(req.email());
        s.setPhone(req.phone());
        if (req.invoicePrefix() != null && !req.invoicePrefix().isBlank())
            s.setInvoicePrefix(req.invoicePrefix());
        if (req.defaultDueDays() != null)
            s.setDefaultDueDays(req.defaultDueDays());
        s.setPan(req.pan());
        s.setStateCode(req.stateCode());
        s.setBankName(req.bankName());
        s.setBankAccount(req.bankAccount());
        s.setBankIfsc(req.bankIfsc());
        s.setBankBranch(req.bankBranch());
        s.setUpiId(req.upiId());
        s.setDefaultTerms(req.defaultTerms());
        s.setDefaultNotes(req.defaultNotes());
        return toResponse(repo.save(s));
    }

    CompanySettings getOrCreate() {
        return repo.findFirst().orElseGet(() -> {
            CompanySettings s = new CompanySettings();
            s.setCompanyName("My Company");
            return repo.save(s);
        });
    }

    private CompanySettingsResponse toResponse(CompanySettings s) {
        return new CompanySettingsResponse(
                s.getId(), s.getCompanyName(), s.getAddress(), s.getGstNumber(),
                s.getEmail(), s.getPhone(), s.getInvoicePrefix(),
                s.getNextInvoiceNo(), s.getDefaultDueDays(),
                s.getPan(), s.getStateCode(),
                s.getBankName(), s.getBankAccount(), s.getBankIfsc(), s.getBankBranch(),
                s.getUpiId(), s.getDefaultTerms(), s.getDefaultNotes());
    }
}
