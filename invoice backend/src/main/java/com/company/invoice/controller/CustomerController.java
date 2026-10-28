package com.company.invoice.controller;

import com.company.invoice.dto.CustomerRequest;
import com.company.invoice.dto.CustomerResponse;
import com.company.invoice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerRequest request) {
        return customerService.create(request);
    }

    @GetMapping
    public Page<CustomerResponse> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return customerService.getAll(q, PageRequest.of(page, size, Sort.by("name")));
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id,
                                   @Valid @RequestBody CustomerRequest request) {
        return customerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }
}