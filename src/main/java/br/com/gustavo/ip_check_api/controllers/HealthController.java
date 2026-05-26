package br.com.gustavo.ip_check_api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Health", description = "API health check endpoint")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Check API status", description = "Returns a simple message indicating that the API is running.")
    public String health() {
        return "IP Check API is running";
    }
}