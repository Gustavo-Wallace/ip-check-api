package br.com.gustavo.ip_check_api.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpAnalysisManualRequestDTO {

    private Boolean vpn;
    private Boolean proxy;
    private Boolean tor;
    private Boolean datacenter;
}