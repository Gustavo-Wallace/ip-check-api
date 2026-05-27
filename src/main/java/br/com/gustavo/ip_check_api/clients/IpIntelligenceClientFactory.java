package br.com.gustavo.ip_check_api.clients;

import org.springframework.stereotype.Component;

import br.com.gustavo.ip_check_api.config.IpIntelligenceProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IpIntelligenceClientFactory {

    private final IpIntelligenceProperties properties;
    private final MockIpIntelligenceClient mockIpIntelligenceClient;
    private final ProxyCheckIpIntelligenceClient proxyCheckIpIntelligenceClient;

    public IpIntelligenceClient getClient() {
        String provider = properties.getProvider();

        if (provider == null || provider.isBlank()) {
            return mockIpIntelligenceClient;
        }

        return switch (provider.toLowerCase()) {
            case "mock" -> mockIpIntelligenceClient;
            case "proxycheck" -> proxyCheckIpIntelligenceClient;
            default -> throw new IllegalArgumentException("Unsupported IP intelligence provider: " + provider);
        };
    }
}