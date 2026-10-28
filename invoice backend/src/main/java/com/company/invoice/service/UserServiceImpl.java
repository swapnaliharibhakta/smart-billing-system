package com.company.invoice.service;

import com.company.invoice.dto.RegisterRequest;
import com.company.invoice.dto.UserResponse;
import com.company.invoice.entity.User.Role;
import com.company.invoice.entity.User;
import com.company.invoice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse create(RegisterRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user=new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRole(Role.STAFF);
        user.setEnabled(true);
        return toResponse(userRepository.save(user));
    }
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Override
    @Transactional
    public UserResponse setEnabled(Long id, boolean enabled) {
        User user = find(id);
        user.setEnabled(enabled);
        return toResponse(userRepository.save(user));
    }

    private User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(),
                u.getPhone(), u.getRole().name(), u.isEnabled());
    }
}
