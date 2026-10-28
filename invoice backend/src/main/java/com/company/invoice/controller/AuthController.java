package com.company.invoice.controller;

import com.company.invoice.entity.User;
import com.company.invoice.repository.UserRepository;
import com.company.invoice.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record UserInfo(Long id, String name, String email, String role) {}

    public record LoginResponse(String token, UserInfo user) {}

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim())
                .filter(User::isEnabled)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        return new LoginResponse(
                jwtService.generateToken(user),
                new UserInfo(user.getId(), user.getName(), user.getEmail(), user.getRole().name()));
    }
}