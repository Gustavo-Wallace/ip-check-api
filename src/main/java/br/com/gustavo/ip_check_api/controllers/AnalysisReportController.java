package br.com.gustavo.ip_check_api.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavo.ip_check_api.dtos.AnalysisSummaryReportDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.services.IpAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/analyses")
@RequiredArgsConstructor
@Tag(name = "Analysis Reports", description = "Endpoints for analysis history and reports")
public class AnalysisReportController {

    private final IpAnalysisService ipAnalysisService;

    @GetMapping
    @Operation(summary = "List all IP analyses", description = "Returns all IP analyses stored in the database.")
    public List<IpAnalysisResponseDTO> findAll() {
        return ipAnalysisService.findAll();
    }

    @GetMapping("/report/risk-level")
    @Operation(summary = "Generate risk level report", description = "Returns the amount of analyses grouped by risk level.")
    public Map<RiskLevel, Long> countByRiskLevel() {
        return ipAnalysisService.countByRiskLevel();
    }

    @GetMapping("/report/anonymity")
    @Operation(summary = "Generate anonymity indicators report", description = "Returns the amount of analyses grouped by VPN, proxy, Tor, datacenter and anonymous indicators.")
    public Map<String, Long> countByAnonymityIndicators() {
        return ipAnalysisService.countByAnonymityIndicators();
    }

    @GetMapping("/report/summary")
    @Operation(summary = "Generate analysis summary report", description = "Returns a consolidated summary of all IP analyses.")
    public AnalysisSummaryReportDTO getSummaryReport() {
        return ipAnalysisService.getSummaryReport();
    }
}