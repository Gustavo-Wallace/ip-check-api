package br.com.gustavo.ip_check_api.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavo.ip_check_api.dtos.IpAnalysisManualRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.services.IpAnalysisService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ips")
@RequiredArgsConstructor
public class IpAnalysisController {

    private final IpAnalysisService ipAnalysisService;

    @PostMapping("/{address}/analyze")
    public IpAnalysisResponseDTO analyze(@PathVariable String address) {
        return ipAnalysisService.analyze(address);
    }

    @GetMapping("/{address}/analyses")
    public List<IpAnalysisResponseDTO> findByAddress(@PathVariable String address) {
        return ipAnalysisService.findByAddress(address);
    }

    @PostMapping("/{address}/analyze/manual")
    public IpAnalysisResponseDTO analyzeManually(
            @PathVariable String address,
            @RequestBody IpAnalysisManualRequestDTO requestDTO
    ) {
        return ipAnalysisService.analyzeManually(address, requestDTO);
    }

    @GetMapping("/analyses")
    public List<IpAnalysisResponseDTO> findAll() {
        return ipAnalysisService.findAll();
    }

    @GetMapping("/analyses/report/risk-level")
    public Map<RiskLevel, Long> countByRiskLevel() {
        return ipAnalysisService.countByRiskLevel();
    }

    @GetMapping("/analyses/report/anonymity")
    public Map<String, Long> countByAnonymityIndicators() {
        return ipAnalysisService.countByAnonymityIndicators();
    }

    
}