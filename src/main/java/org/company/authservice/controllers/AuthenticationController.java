package org.company.authservice.controllers;

import org.company.authservice.dto.AuthResponse;
import org.company.authservice.dto.LoginRequest;
import org.company.authservice.dto.RegisterRequest;
import org.company.authservice.entity.User;
import org.company.authservice.repository.UserRepository;
import org.company.authservice.security.TokenService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping(value = "/auth", produces = {"application/json"})
public class AuthenticationController {


    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    private TokenService tokenService;


    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        var credentials = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        var authentication = authenticationManager.authenticate(credentials);

        var token = tokenService.generateToken((User) authentication.getPrincipal());

        return ResponseEntity.ok(new AuthResponse(token));

    }



    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());
        Instant instant = Instant.now();
        User user = new User(request.email(), encryptedPassword, request.role(), instant);

        this.userRepository.save(user);

        return ResponseEntity.ok().build();

    }
}
