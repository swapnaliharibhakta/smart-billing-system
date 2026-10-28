package com.company.invoice.service;

import com.company.invoice.dto.CustomerRequest;
import com.company.invoice.dto.CustomerResponse;
import com.company.invoice.entity.Customer;
import com.company.invoice.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        checkPhone(request.phone(), null);
        Customer customer = new Customer();
        apply(customer, request);
        return toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAll(String q, Pageable pageable) {
        String query = (q == null) ? "" : q.trim();
        return customerRepository.search(query, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = find(id);
        checkPhone(request.phone(), id);
        apply(customer, request);
        return toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer customer = find(id);
        customer.setActive(false);
        customerRepository.save(customer);
    }

    private void checkPhone(String phone, Long id) {
        if (phone == null || phone.isBlank()) return;
        boolean exists = (id == null)
                ? customerRepository.existsByPhone(phone)
                : customerRepository.existsByPhoneAndIdNot(phone, id);
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already exists");
        }
    }

    private void apply(Customer c, CustomerRequest r) {
        c.setName(r.name());
        c.setEmail(r.email());
        c.setPhone(r.phone());
        c.setAddress(r.address());
        c.setGstNumber(r.gstNumber());
        c.setBillingStreet(r.billingStreet());
        c.setBillingCity(r.billingCity());
        c.setBillingState(r.billingState());
        c.setBillingStateCode(r.billingStateCode());
        c.setBillingPincode(r.billingPincode());
        c.setShippingName(r.shippingName());
        c.setShippingStreet(r.shippingStreet());
        c.setShippingCity(r.shippingCity());
        c.setShippingState(r.shippingState());
        c.setShippingStateCode(r.shippingStateCode());
        c.setShippingPincode(r.shippingPincode());
    }

    private Customer find(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                c.getAddress(), c.getGstNumber(), c.isActive(), c.getCreatedAt(),
                c.getBillingStreet(), c.getBillingCity(), c.getBillingState(),
                c.getBillingStateCode(), c.getBillingPincode(),
                c.getShippingName(), c.getShippingStreet(), c.getShippingCity(),
                c.getShippingState(), c.getShippingStateCode(), c.getShippingPincode());
    }
}
