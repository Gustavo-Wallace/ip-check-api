package br.com.gustavo.ip_check_api.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.gustavo.ip_check_api.clients.IpIntelligenceClientFactory;
import br.com.gustavo.ip_check_api.dtos.AnalysisSummaryReportDTO;
import br.com.gustavo.ip_check_api.dtos.BatchAnalysisErrorDTO;
import br.com.gustavo.ip_check_api.dtos.BatchAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.dtos.ExternalIpCheckResponseDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisManualRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.enums.AnalysisSource;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.models.IpAddress;
import br.com.gustavo.ip_check_api.models.IpAnalysis;
import br.com.gustavo.ip_check_api.repositories.IpAddressRepository;
import br.com.gustavo.ip_check_api.repositories.IpAnalysisRepository;
import br.com.gustavo.ip_check_api.utils.IpValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpAnalysisService {

        private final IpAnalysisRepository ipAnalysisRepository;
        private final IpIntelligenceClientFactory ipIntelligenceClientFactory;
        private final IpAddressRepository ipAddressRepository;

        public IpAnalysisResponseDTO analyze(String address) {
                IpValidator.validate(address);

                ExternalIpCheckResponseDTO externalResponse = ipIntelligenceClientFactory.getClient().check(address);

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
                        Boolean anonymous) {
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
                                                Collectors.counting()));

                Arrays.stream(RiskLevel.values())
                                .forEach(riskLevel -> report.putIfAbsent(riskLevel, 0L));

                return report;
        }

        public Map<String, Long> countByAnonymityIndicators() {
                List<IpAnalysis> analyses = ipAnalysisRepository.findAll();

                long vpnCount = analyses.stream()
                                .filter(analysis -> Boolean.TRUE.equals(analysis.getVpn()))
                                .count();

                long proxyCount = analyses.stream()
                                .filter(analysis -> Boolean.TRUE.equals(analysis.getProxy()))
                                .count();

                long torCount = analyses.stream()
                                .filter(analysis -> Boolean.TRUE.equals(analysis.getTor()))
                                .count();

                long datacenterCount = analyses.stream()
                                .filter(analysis -> Boolean.TRUE.equals(analysis.getDatacenter()))
                                .count();

                long anonymousCount = analyses.stream()
                                .filter(analysis -> Boolean.TRUE.equals(analysis.getAnonymous()))
                                .count();

                return Map.of(
                                "vpn", vpnCount,
                                "proxy", proxyCount,
                                "tor", torCount,
                                "datacenter", datacenterCount,
                                "anonymous", anonymousCount);
        }

        public BatchAnalysisResponseDTO analyzeActiveIpAddresses() {
                List<IpAddress> activeIpAddresses = ipAddressRepository.findByActiveTrue();

                List<IpAnalysisResponseDTO> analyses = new ArrayList<>();
                List<BatchAnalysisErrorDTO> errors = new ArrayList<>();

                for (IpAddress ipAddress : activeIpAddresses) {
                        try {
                                IpAnalysisResponseDTO analysis = analyze(ipAddress.getAddress());
                                analyses.add(analysis);
                        } catch (Exception exception) {
                                BatchAnalysisErrorDTO error = BatchAnalysisErrorDTO.builder()
                                                .address(ipAddress.getAddress())
                                                .message(exception.getMessage())
                                                .build();

                                errors.add(error);
                        }
                }

                return BatchAnalysisResponseDTO.builder()
                                .totalProcessed(activeIpAddresses.size())
                                .successCount(analyses.size())
                                .errorCount(errors.size())
                                .analyses(analyses)
                                .errors(errors)
                                .build();
        }

        public AnalysisSummaryReportDTO getSummaryReport() {
                List<IpAnalysis> analyses = ipAnalysisRepository.findAll();

                long anonymousCount = analyses.stream()
                                .filter(analysis -> Boolean.TRUE.equals(analysis.getAnonymous()))
                                .count();

                long vpnCount = analyses.stream()
                                .filter(analysis -> Boolean.TRUE.equals(analysis.getVpn()))
                                .count();

                long proxyCount = analyses.stream()
                                .filter(analysis -> Boolean.TRUE.equals(analysis.getProxy()))
                                .count();

                long torCount = analyses.stream()
                                .filter(analysis -> Boolean.TRUE.equals(analysis.getTor()))
                                .count();

                long datacenterCount = analyses.stream()
                                .filter(analysis -> Boolean.TRUE.equals(analysis.getDatacenter()))
                                .count();

                RiskLevel highestRiskLevel = analyses.stream()
                                .map(IpAnalysis::getRiskLevel)
                                .max(this::compareRiskLevel)
                                .orElse(RiskLevel.LOW);

                return AnalysisSummaryReportDTO.builder()
                                .totalAnalyses((long) analyses.size())
                                .anonymousCount(anonymousCount)
                                .vpnCount(vpnCount)
                                .proxyCount(proxyCount)
                                .torCount(torCount)
                                .datacenterCount(datacenterCount)
                                .highestRiskLevel(highestRiskLevel)
                                .build();
        }

        private int compareRiskLevel(RiskLevel first, RiskLevel second) {
                return Integer.compare(getRiskWeight(first), getRiskWeight(second));
        }

        private int getRiskWeight(RiskLevel riskLevel) {
                return switch (riskLevel) {
                        case LOW -> 1;
                        case ATTENTION -> 2;
                        case MEDIUM -> 3;
                        case HIGH -> 4;
                        case CRITICAL -> 5;
                };
        }

        public Page<IpAnalysisResponseDTO> findAllPaged(Pageable pageable) {
                return ipAnalysisRepository.findAll(pageable)
                                .map(this::toResponseDTO);
        }

}