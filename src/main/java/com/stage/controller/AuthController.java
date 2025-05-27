package com.stage.controller;

import com.stage.dto.AuthResponseDTO;
import com.stage.persistans.User;
import com.stage.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody User user) {
        String token = authService.login(user.getEmail(), user.getPassword());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

}
