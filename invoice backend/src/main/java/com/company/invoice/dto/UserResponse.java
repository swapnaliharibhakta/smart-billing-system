package com.company.invoice.dto;

public record UserResponse(Long id, String name, String email,
                           String phone, String role, boolean enabled) {

}