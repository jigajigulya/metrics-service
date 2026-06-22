package dev.matveev.metricsservice.controller;

import dev.matveev.metricsservice.dto.AuthReq;
import dev.matveev.metricsservice.dto.AuthResponse;
import dev.matveev.metricsservice.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;


    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> auth(@Valid @RequestBody AuthReq authReq) {
        String token  = jwtService.generateToken(authReq.clientID());
        return ResponseEntity.ok(new AuthResponse(token));
    }

}
