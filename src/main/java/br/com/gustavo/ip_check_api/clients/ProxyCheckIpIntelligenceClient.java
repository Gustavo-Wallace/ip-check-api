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

        Map<?, ?> network = getMap(ipData.get("network"));
        Map<?, ?> detections = getMap(ipData.get("detections"));

        String externalProvider = getString(network.get("provider"));
        String type = getString(network.get("type"));

        boolean proxy = getBoolean(detections.get("proxy"));
        boolean vpn = getBoolean(detections.get("vpn"));
        boolean tor = getBoolean(detections.get("tor"));
        boolean hosting = getBoolean(detections.get("hosting"));
        boolean anonymous = getBoolean(detections.get("anonymous"));

        Integer risk = getInteger(detections.get("risk"));

        boolean datacenter = hosting || isDatacenterType(type);

        return ExternalIpCheckResponseDTO.builder()
                .address(address)
                .vpn(vpn)
                .proxy(proxy)
                .tor(tor)
                .datacenter(datacenter)
                .externalRiskScore(risk)
                .externalType(type)
                .externalProvider(externalProvider)
                .build();
    }

    private Map<?, ?> getMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return map;
        }

        return Map.of();
    }

    private boolean getBoolean(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }

        return "true".equalsIgnoreCase(value.toString())
                || "yes".equalsIgnoreCase(value.toString());
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

    private boolean isDatacenterType(String type) {
        if (type == null) {
            return false;
        }

        String normalizedType = type.trim().toLowerCase();

        return normalizedType.contains("hosting")
                || normalizedType.contains("data center")
                || normalizedType.contains("datacenter")
                || normalizedType.contains("server");
    }
}