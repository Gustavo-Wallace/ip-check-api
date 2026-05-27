package br.com.gustavo.ip_check_api.clients;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.gustavo.ip_check_api.config.IpIntelligenceProperties;
import br.com.gustavo.ip_check_api.dtos.ExternalIpCheckResponseDTO;
import br.com.gustavo.ip_check_api.exceptions.ExternalIpIntelligenceException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProxyCheckIpIntelligenceClient implements IpIntelligenceClient {

    private final WebClient.Builder webClientBuilder;
    private final IpIntelligenceProperties properties;

    @Override
    public ExternalIpCheckResponseDTO check(String address) {
        try {
            Map response = webClientBuilder
                    .baseUrl(properties.getBaseUrl())
                    .build()
                    .get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/{address}")
                                .queryParam("vpn", "1")
                                .queryParam("risk", "1");

                        if (properties.getApiKey() != null && !properties.getApiKey().isBlank()) {
                            uriBuilder.queryParam("key", properties.getApiKey());
                        }

                        return uriBuilder.build(address);
                    })
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return mapResponse(address, response);
        } catch (Exception exception) {
            throw new ExternalIpIntelligenceException("Failed to check IP using ProxyCheck", exception);
        }
    }

    private ExternalIpCheckResponseDTO mapResponse(String address, Map response) {
        if (response == null) {
            throw new ExternalIpIntelligenceException("Empty response from ProxyCheck");
        }

        Object status = response.get("status");

        if (status != null && !"ok".equalsIgnoreCase(status.toString())) {
            throw new ExternalIpIntelligenceException("ProxyCheck returned status: " + status);
        }

        Object ipDataObject = response.get(address);

        if (!(ipDataObject instanceof Map<?, ?> ipData)) {
            throw new ExternalIpIntelligenceException("Invalid response format from ProxyCheck");
        }

        boolean proxy = isYes(ipData.get("proxy"));
        String type = getString(ipData.get("type"));
        Integer risk = getInteger(ipData.get("risk"));

        boolean vpn = "VPN".equalsIgnoreCase(type);
        boolean tor = "TOR".equalsIgnoreCase(type);
        boolean datacenter = risk != null && risk >= 33 && !proxy && !vpn && !tor;

        return ExternalIpCheckResponseDTO.builder()
                .address(address)
                .vpn(vpn)
                .proxy(proxy)
                .tor(tor)
                .datacenter(datacenter)
                .build();
    }

    private boolean isYes(Object value) {
        return value != null && "yes".equalsIgnoreCase(value.toString());
    }

    private String getString(Object value) {
        return value == null ? null : value.toString();
    }

    private Integer getInteger(Object value) {
        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}