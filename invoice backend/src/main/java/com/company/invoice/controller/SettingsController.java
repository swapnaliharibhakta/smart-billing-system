package com.company.invoice.controller;

import com.company.invoice.dto.CompanySettingsRequest;
import com.company.invoice.dto.CompanySettingsResponse;
import com.company.invoice.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService service;

    public SettingsController(SettingsService service) {
        this.service = service;
    }

    @GetMapping
    public CompanySettingsResponse get() {
        return service.get();
    }

    @PutMapping
    public CompanySettingsResponse save(@Valid @RequestBody CompanySettingsRequest request) {
        return service.save(request);
    }
}
