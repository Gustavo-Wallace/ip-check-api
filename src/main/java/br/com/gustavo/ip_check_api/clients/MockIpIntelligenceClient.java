package br.com.gustavo.ip_check_api.clients;

import org.springframework.stereotype.Component;

import br.com.gustavo.ip_check_api.dtos.ExternalIpCheckResponseDTO;

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
                .externalRiskScore(0)
                .externalType("MOCK")
                .externalProvider("Mock Provider")
                .asn("MOCK-ASN")
                .country("Mock Country")
                .city("Mock City")
                .build();
    }

    private Boolean isProbablyDatacenter(String address) {
        return address.equals("8.8.8.8")
                || address.equals("1.1.1.1")
                || address.equals("9.9.9.9");
    }
}