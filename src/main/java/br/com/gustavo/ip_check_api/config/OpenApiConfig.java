package br.com.gustavo.ip_check_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ipCheckApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IP Check API")
                        .version("1.0.0")
                        .description("REST API for detecting VPN, proxy, Tor, datacenter and anonymized IP addresses."));
    }
}