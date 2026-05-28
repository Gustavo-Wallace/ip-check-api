package br.com.gustavo.ip_check_api.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavo.ip_check_api.dtos.AnalysisSummaryReportDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.services.IpAnalysisService;
import br.com.gustavo.ip_check_api.utils.RiskLevelParser;
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

    @GetMapping("/page")
    @Operation(summary = "List all IP analyses with pagination", description = "Returns all IP analyses using pagination.")
    public Page<IpAnalysisResponseDTO> findAllPaged(Pageable pageable) {
        return ipAnalysisService.findAllPaged(pageable);
    }

    @GetMapping("/risk-level/{riskLevel}")
    @Operation(summary = "List analyses by risk level", description = "Returns all IP analyses matching the provided risk level. Accepts values such as LOW, low, CRITICAL or critical.")
    public List<IpAnalysisResponseDTO> findByRiskLevel(@PathVariable String riskLevel) {
        RiskLevel parsedRiskLevel = RiskLevelParser.parse(riskLevel);

        return ipAnalysisService.findByRiskLevel(parsedRiskLevel);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter analyses by anonymity indicators", description = "Returns IP analyses filtered by optional VPN, proxy, Tor, datacenter and anonymous indicators.")
    public List<IpAnalysisResponseDTO> filterByIndicators(
            @RequestParam(required = false) Boolean vpn,
            @RequestParam(required = false) Boolean proxy,
            @RequestParam(required = false) Boolean tor,
            @RequestParam(required = false) Boolean datacenter,
            @RequestParam(required = false) Boolean anonymous) {
        return ipAnalysisService.filterByIndicators(vpn, proxy, tor, datacenter, anonymous);
    }

    @GetMapping("/country/{country}")
    @Operation(summary = "List analyses by country", description = "Returns all IP analyses matching the provided country.")
    public List<IpAnalysisResponseDTO> findByCountry(@PathVariable String country) {
        return ipAnalysisService.findByCountry(country);
    }

    @GetMapping("/provider/{externalProvider}")
    @Operation(summary = "List analyses by external provider", description = "Returns all IP analyses whose external provider contains the provided value.")
    public List<IpAnalysisResponseDTO> findByExternalProvider(@PathVariable String externalProvider) {
        return ipAnalysisService.findByExternalProvider(externalProvider);
    }

    @GetMapping("/asn/{asn}")
    @Operation(summary = "List analyses by ASN", description = "Returns all IP analyses matching the provided ASN.")
    public List<IpAnalysisResponseDTO> findByAsn(@PathVariable String asn) {
        return ipAnalysisService.findByAsn(asn);
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