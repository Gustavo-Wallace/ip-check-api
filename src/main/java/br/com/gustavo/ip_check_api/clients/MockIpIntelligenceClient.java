package br.com.gustavo.ip_check_api.clients;

import br.com.gustavo.ip_check_api.dtos.ExternalIpCheckResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MockIpIntelligenceClient implements IpIntelligenceClient {

    @Override
    public ExternalIpCheckResponseDTO check(String address) {
        return ExternalIpCheckResponseDTO.builder()
                .address(address)
                .vpn(false)
                .proxy(false)
                .tor(false)
                .datacenter(isProbablyDatacenter(address))
                .build();
    }

    private Boolean isProbablyDatacenter(String address) {
        return address.equals("8.8.8.8")
                || address.equals("1.1.1.1")
                || address.equals("9.9.9.9");
    }
}