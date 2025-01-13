package com.mandamong.api.health.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        final HttpStatus value = HttpStatus.ACCEPTED;
        final String message = "Server is alive" + System.lineSeparator();
        return ResponseEntity.status(value).body(message);
    }
}
