package com.company.invoice.controller;

import com.company.invoice.dto.RegisterRequest;
import com.company.invoice.dto.UserResponse;
import com.company.invoice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody RegisterRequest request) {
        return userService.create(request);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PatchMapping("/{id}/enabled")
    public UserResponse setEnabled(@PathVariable Long id, @RequestParam boolean value) {
        return userService.setEnabled(id, value);
    }
}
