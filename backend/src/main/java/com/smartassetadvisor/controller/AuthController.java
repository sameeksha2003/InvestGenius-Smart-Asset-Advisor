package com.smartassetadvisor.controller;

import com.smartassetadvisor.dto.AuthRequest;
import com.smartassetadvisor.dto.AuthResponse;
import com.smartassetadvisor.dto.LoginRequest;
import com.smartassetadvisor.dto.LoginResponse;
import com.smartassetadvisor.model.User;
import com.smartassetadvisor.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response); 
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    System.out.println("🔵 Login Attempt: " + request.getEmail());

    LoginResponse response = authService.login(request);
    if (response == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    System.out.println("🟢 Logged In User: " + response.getUser().getEmail());

    return ResponseEntity.ok(response); 
    }


    @GetMapping("/user")
    public ResponseEntity<User> getUserDetails(@RequestParam String email) {
        User user = authService.getUserByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
