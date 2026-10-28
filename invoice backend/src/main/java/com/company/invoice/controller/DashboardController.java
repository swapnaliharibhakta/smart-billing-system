package com.company.invoice.controller;

import com.company.invoice.dto.DashboardResponse;
import com.company.invoice.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) { this.service = service; }

    @GetMapping("/summary")
    public DashboardResponse summary() { return service.getSummary(); }
}