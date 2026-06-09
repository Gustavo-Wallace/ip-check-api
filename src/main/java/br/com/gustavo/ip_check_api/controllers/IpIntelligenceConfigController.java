package br.com.gustavo.ip_check_api.controllers;

import br.com.gustavo.ip_check_api.config.IpIntelligenceProperties;
import br.com.gustavo.ip_check_api.dtos.IpIntelligenceConfigResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "IP Intelligence Config", description = "Endpoint for checking the active IP intelligence configuration")
public class IpIntelligenceConfigController {

    private final IpIntelligenceProperties properties;

    @GetMapping("/ip-intelligence/config")
    @Operation(summary = "Check IP intelligence configuration", description = "Returns the active provider and whether base URL and API key are configured.")
    public IpIntelligenceConfigResponseDTO getConfig() {
        return IpIntelligenceConfigResponseDTO.builder()
                .provider(properties.getProvider())
                .baseUrlConfigured(properties.getBaseUrl() != null
                        && !properties.getBaseUrl().isBlank())
                .apiKeyConfigured(properties.getApiKey() != null
                        && !properties.getApiKey().isBlank())
                .cacheDurationMinutes(properties.getCacheDurationMinutes())
                .build();
    }
}