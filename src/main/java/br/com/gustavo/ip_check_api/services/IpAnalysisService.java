package br.com.gustavo.ip_check_api.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.gustavo.ip_check_api.clients.IpIntelligenceClient;
import br.com.gustavo.ip_check_api.dtos.ExternalIpCheckResponseDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisManualRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.enums.AnalysisSource;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.models.IpAnalysis;
import br.com.gustavo.ip_check_api.repositories.IpAnalysisRepository;
import br.com.gustavo.ip_check_api.utils.IpValidator;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class IpAnalysisService {

    private final IpAnalysisRepository ipAnalysisRepository;
    private final IpIntelligenceClient ipIntelligenceClient;

    public IpAnalysisResponseDTO analyze(String address) {
        IpValidator.validate(address);

        ExternalIpCheckResponseDTO externalResponse = ipIntelligenceClient.check(address);

        Boolean vpn = Boolean.TRUE.equals(externalResponse.getVpn());
        Boolean proxy = Boolean.TRUE.equals(externalResponse.getProxy());
        Boolean tor = Boolean.TRUE.equals(externalResponse.getTor());
        Boolean datacenter = Boolean.TRUE.equals(externalResponse.getDatacenter());
        Boolean anonymous = vpn || proxy || tor;

        RiskLevel riskLevel = calculateRiskLevel(vpn, proxy, tor, datacenter, anonymous);

        IpAnalysis ipAnalysis = IpAnalysis.builder()
                .address(address)
                .vpn(vpn)
                .proxy(proxy)
                .tor(tor)
                .datacenter(datacenter)
                .anonymous(anonymous)
                .riskLevel(riskLevel)
                .source(AnalysisSource.EXTERNAL_API)
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

    public List<IpAnalysisResponseDTO> findByAddress(String address) {
        IpValidator.validate(address);

        return ipAnalysisRepository.findByAddress(address)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private RiskLevel calculateRiskLevel(
            Boolean vpn,
            Boolean proxy,
            Boolean tor,
            Boolean datacenter,
            Boolean anonymous
    ) {
        if (Boolean.TRUE.equals(tor)) {
            return RiskLevel.CRITICAL;
        }

        if (Boolean.TRUE.equals(anonymous) && Boolean.TRUE.equals(proxy)) {
            return RiskLevel.HIGH;
        }

        if (Boolean.TRUE.equals(vpn) && Boolean.TRUE.equals(proxy)) {
            return RiskLevel.HIGH;
        }

        if (Boolean.TRUE.equals(vpn) || Boolean.TRUE.equals(proxy)) {
            return RiskLevel.MEDIUM;
        }

        if (Boolean.TRUE.equals(datacenter)) {
            return RiskLevel.ATTENTION;
        }

        return RiskLevel.LOW;
    }

    public IpAnalysisResponseDTO analyzeManually(String address, IpAnalysisManualRequestDTO requestDTO) {
        IpValidator.validate(address);

        Boolean vpn = Boolean.TRUE.equals(requestDTO.getVpn());
        Boolean proxy = Boolean.TRUE.equals(requestDTO.getProxy());
        Boolean tor = Boolean.TRUE.equals(requestDTO.getTor());
        Boolean datacenter = Boolean.TRUE.equals(requestDTO.getDatacenter());
        Boolean anonymous = vpn || proxy || tor;

        RiskLevel riskLevel = calculateRiskLevel(vpn, proxy, tor, datacenter, anonymous);

        IpAnalysis ipAnalysis = IpAnalysis.builder()
                .address(address)
                .vpn(vpn)
                .proxy(proxy)
                .tor(tor)
                .datacenter(datacenter)
                .anonymous(anonymous)
                .riskLevel(riskLevel)
                .source(AnalysisSource.MANUAL_SIMULATION)
                .build();

        IpAnalysis savedIpAnalysis = ipAnalysisRepository.save(ipAnalysis);

        return toResponseDTO(savedIpAnalysis);
    }

    public List<IpAnalysisResponseDTO> findAll() {
        return ipAnalysisRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Map<RiskLevel, Long> countByRiskLevel() {
        Map<RiskLevel, Long> report = ipAnalysisRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        IpAnalysis::getRiskLevel,
                        Collectors.counting()
                ));

        Arrays.stream(RiskLevel.values())
                .forEach(riskLevel -> report.putIfAbsent(riskLevel, 0L));

        return report;
    }

}