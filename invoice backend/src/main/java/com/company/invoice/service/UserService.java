package com.company.invoice.service;

import com.company.invoice.dto.RegisterRequest;
import com.company.invoice.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse create(RegisterRequest request);

    List<UserResponse> getAll();

    UserResponse getById(Long Id);

    UserResponse setEnabled(Long Id, boolean enabled);
}
