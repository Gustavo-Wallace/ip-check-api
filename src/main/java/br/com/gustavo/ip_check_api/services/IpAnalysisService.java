package br.com.gustavo.ip_check_api.services;

import org.springframework.stereotype.Service;

import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.models.IpAnalysis;
import br.com.gustavo.ip_check_api.repositories.IpAnalysisRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpAnalysisService {

    private final IpAnalysisRepository ipAnalysisRepository;

    public IpAnalysisResponseDTO analyze(String address) {
        IpAnalysis ipAnalysis = IpAnalysis.builder()
                .address(address)
                .vpn(false)
                .proxy(false)
                .tor(false)
                .datacenter(false)
                .anonymous(false)
                .riskLevel(RiskLevel.LOW)
                .source("INTERNAL_RULES")
                .build();

        IpAnalysis savedIpAnalysis = ipAnalysisRepository.save(ipAnalysis);

        return toResponseDTO(savedIpAnalysis);
    }

    private IpAnalysisResponseDTO toResponseDTO(IpAnalysis ipAnalysis) {
        return IpAnalysisResponseDTO.builder()
                .id(ipAnalysis.getId())
                .address(ipAnalysis.getAddress())
                .vpn(ipAnalysis.getVpn())
                .proxy(ipAnalysis.getProxy())
                .tor(ipAnalysis.getTor())
                .datacenter(ipAnalysis.getDatacenter())
                .anonymous(ipAnalysis.getAnonymous())
                .riskLevel(ipAnalysis.getRiskLevel())
                .source(ipAnalysis.getSource())
                .analyzedAt(ipAnalysis.getAnalyzedAt())
                .build();
    }
}