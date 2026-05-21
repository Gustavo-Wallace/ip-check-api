package br.com.gustavo.ip_check_api.clients;

import br.com.gustavo.ip_check_api.dtos.ExternalIpCheckResponseDTO;

public interface IpIntelligenceClient {

    ExternalIpCheckResponseDTO check(String address);
}