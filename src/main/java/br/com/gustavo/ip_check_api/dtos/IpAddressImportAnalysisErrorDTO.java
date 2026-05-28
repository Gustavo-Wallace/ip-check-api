package br.com.gustavo.ip_check_api.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IpAddressImportAnalysisErrorDTO {

    private String address;
    private String message;
}