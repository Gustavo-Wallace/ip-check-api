package br.com.gustavo.ip_check_api.dtos;

import java.time.LocalDateTime;

import br.com.gustavo.ip_check_api.enums.AnalysisSource;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IpAnalysisResponseDTO {

    private Long id;
    private String address;

    private Boolean vpn;
    private Boolean proxy;
    private Boolean tor;
    private Boolean datacenter;
    private Boolean anonymous;

    private RiskLevel riskLevel;
    private AnalysisSource source;

    private Integer externalRiskScore;
    private String externalType;
    private String externalProvider;

    private LocalDateTime analyzedAt;
}