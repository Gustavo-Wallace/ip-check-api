package br.com.gustavo.ip_check_api.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExternalIpCheckResponseDTO {

    private String address;
    private Boolean vpn;
    private Boolean proxy;
    private Boolean tor;
    private Boolean datacenter;
}