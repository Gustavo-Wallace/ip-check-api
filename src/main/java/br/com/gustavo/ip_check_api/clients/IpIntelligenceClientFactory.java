package br.com.gustavo.ip_check_api.clients;

import br.com.gustavo.ip_check_api.config.IpIntelligenceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IpIntelligenceClientFactory {

    private final IpIntelligenceProperties properties;
    private final MockIpIntelligenceClient mockIpIntelligenceClient;

    public IpIntelligenceClient getClient() {
        String provider = properties.getProvider();

        if (provider == null || provider.isBlank()) {
            return mockIpIntelligenceClient;
        }

        return switch (provider.toLowerCase()) {
            case "mock" -> mockIpIntelligenceClient;
            default -> throw new IllegalArgumentException("Unsupported IP intelligence provider: " + provider);
        };
    }
}