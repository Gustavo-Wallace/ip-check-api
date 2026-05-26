package br.com.gustavo.ip_check_api.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavo.ip_check_api.dtos.IpAnalysisManualRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.services.IpAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ips")
@RequiredArgsConstructor
@Tag(name = "IP Analyses", description = "Endpoints for IP analysis, history and reports")
public class IpAnalysisController {

    private final IpAnalysisService ipAnalysisService;

    @PostMapping("/active/analyze")
    @Operation(summary = "Analyze all active IP addresses", description = "Analyzes all active registered IP addresses and stores each analysis result.")
    public List<IpAnalysisResponseDTO> analyzeActiveIpAddresses() {
        return ipAnalysisService.analyzeActiveIpAddresses();
    }

    @PostMapping("/{address}/analyze")
    @Operation(summary = "Analyze an IP address", description = "Analyzes an IP address using the configured IP intelligence client.")
    public IpAnalysisResponseDTO analyze(@PathVariable String address) {
        return ipAnalysisService.analyze(address);
    }

    @GetMapping("/{address}/analyses")
    @Operation(summary = "List analyses by IP address", description = "Returns the analysis history for a specific IP address.")
    public List<IpAnalysisResponseDTO> findByAddress(@PathVariable String address) {
        return ipAnalysisService.findByAddress(address);
    }

    @PostMapping("/{address}/analyze/manual")
    @Operation(summary = "Manually simulate an IP analysis", description = "Allows manual simulation of VPN, proxy, Tor and datacenter indicators.")
    public IpAnalysisResponseDTO analyzeManually(
            @PathVariable String address,
            @RequestBody IpAnalysisManualRequestDTO requestDTO) {
        return ipAnalysisService.analyzeManually(address, requestDTO);
    }

}