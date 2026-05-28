package br.com.gustavo.ip_check_api.utils;

import br.com.gustavo.ip_check_api.enums.RiskLevel;

public class RiskLevelParser {

    private RiskLevelParser() {
    }

    public static RiskLevel parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Risk level is required");
        }

        try {
            return RiskLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid risk level: " + value);
        }
    }
}