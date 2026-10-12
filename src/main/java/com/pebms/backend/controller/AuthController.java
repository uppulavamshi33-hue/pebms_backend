package com.pebms.backend.controller;

import com.pebms.backend.dto.AuthResponse;
import com.pebms.backend.dto.LoginRequest;
import com.pebms.backend.dto.RegisterRequest;
import com.pebms.backend.dto.UserDto;
import com.pebms.backend.model.User;
import com.pebms.backend.security.JwtUtils;
import com.pebms.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse res = authService.register(req);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse res = authService.login(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/demo")
    public ResponseEntity<AuthResponse> loginDemo() {
        User demoUser = authService.getOrCreateDemoUser();
        String token = jwtUtils.generateToken(demoUser.getId(), demoUser.getEmail(), demoUser.getName(), demoUser.getRole());
        return ResponseEntity.ok(new AuthResponse(token, authService.mapToDto(demoUser)));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(authService.mapToDto(user));
    }
}
