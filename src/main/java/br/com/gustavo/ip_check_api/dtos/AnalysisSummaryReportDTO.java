package br.com.gustavo.ip_check_api.dtos;

import br.com.gustavo.ip_check_api.enums.RiskLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnalysisSummaryReportDTO {

    private Long totalAnalyses;
    private Long anonymousCount;
    private Long vpnCount;
    private Long proxyCount;
    private Long torCount;
    private Long datacenterCount;
    private RiskLevel highestRiskLevel;
}