package br.com.gustavo.ip_check_api.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IpIntelligenceConfigResponseDTO {

    private String provider;
    private String baseUrlConfigured;
    private String apiKeyConfigured;
    private Long cacheDurationMinutes;
}