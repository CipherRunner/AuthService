package org.company.authservice.dto;

public record RegisterRequest(String email, String password, String role) {
}
