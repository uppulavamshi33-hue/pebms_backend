package com.pebms.backend.service;

import com.pebms.backend.dto.AuthResponse;
import com.pebms.backend.dto.LoginRequest;
import com.pebms.backend.dto.RegisterRequest;
import com.pebms.backend.dto.UserDto;
import com.pebms.backend.exception.ResourceNotFoundException;
import com.pebms.backend.model.User;
import com.pebms.backend.repository.UserRepository;
import com.pebms.backend.security.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail().toLowerCase().trim())) {
            throw new IllegalArgumentException("An account with email " + req.getEmail() + " already exists");
        }

        User user = new User(
                req.getName().trim(),
                req.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(req.getPassword()),
                "ROLE_USER"
        );

        User saved = userRepository.save(user);
        String token = jwtUtils.generateToken(saved.getId(), saved.getEmail(), saved.getName(), saved.getRole());

        return new AuthResponse(token, mapToDto(saved));
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getEmail(), user.getName(), user.getRole());
        return new AuthResponse(token, mapToDto(user));
    }

    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
        }
        // Fallback to default demo user
        return getOrCreateDemoUser();
    }

    public User getOrCreateDemoUser() {
        return userRepository.findByEmail("demo@pebms.com")
                .orElseGet(() -> {
                    User demo = new User(
                            "Alex Demo (Student)",
                            "demo@pebms.com",
                            passwordEncoder.encode("password123"),
                            "ROLE_USER"
                    );
                    return userRepository.save(demo);
                });
    }

    public UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
