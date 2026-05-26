package br.com.gustavo.ip_check_api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ip-intelligence")
public class IpIntelligenceProperties {

    private String provider = "mock";
    private String baseUrl;
    private String apiKey;
}