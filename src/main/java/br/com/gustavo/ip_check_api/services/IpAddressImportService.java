package br.com.gustavo.ip_check_api.services;

import br.com.gustavo.ip_check_api.config.IpImportProperties;
import br.com.gustavo.ip_check_api.dtos.*;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.models.IpAddress;
import br.com.gustavo.ip_check_api.repositories.IpAddressRepository;
import br.com.gustavo.ip_check_api.utils.IpAddressCsvParser;
import br.com.gustavo.ip_check_api.utils.IpValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IpAddressImportService {

    private final IpAddressRepository ipAddressRepository;
    private final IpAnalysisService ipAnalysisService;
    private final IpImportProperties ipImportProperties;
    private final IpAddressService ipAddressService;

    public IpAddressImportResponseDTO importIpAddresses(IpAddressImportRequestDTO requestDTO) {
        List<IpAddressImportItemDTO> items = requestDTO.getAddresses()
                .stream()
                .map(address -> IpAddressImportItemDTO.builder()
                        .address(address)
                        .description(requestDTO.getDescription())
                        .build())
                .toList();

        return importIpAddressItems(items, requestDTO.getAnalyzeAfterImport());
    }

    public IpAddressImportResponseDTO importIpAddressesFromCsvText(IpAddressCsvImportRequestDTO requestDTO) {
        List<IpAddressImportItemDTO> items = IpAddressCsvParser.parse(requestDTO.getCsvContent());

        return importIpAddressItems(items, requestDTO.getAnalyzeAfterImport());
    }

    public IpAddressImportResponseDTO importIpAddressesFromCsvFile(
            MultipartFile file,
            Boolean analyzeAfterImport
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is required");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("File must be a CSV file");
        }

        Long maxCsvFileSizeBytes = ipImportProperties.getMaxCsvFileSizeBytes();

        if (maxCsvFileSizeBytes != null && file.getSize() > maxCsvFileSizeBytes) {
            throw new IllegalArgumentException("CSV file exceeds maximum allowed size");
        }

        try {
            String csvContent = new String(file.getBytes(), StandardCharsets.UTF_8);

            IpAddressCsvImportRequestDTO requestDTO = new IpAddressCsvImportRequestDTO();
            requestDTO.setCsvContent(csvContent);
            requestDTO.setAnalyzeAfterImport(analyzeAfterImport);

            return importIpAddressesFromCsvText(requestDTO);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to read CSV file: " + exception.getMessage());
        }
    }

    private IpAddressImportResponseDTO importIpAddressItems(
            List<IpAddressImportItemDTO> items,
            Boolean analyzeAfterImport
    ) {
        List<IpAddressResponseDTO> imported = new ArrayList<>();
        List<String> duplicated = new ArrayList<>();
        List<IpAddressImportErrorDTO> importErrors = new ArrayList<>();
        List<IpAddressImportAnalysisErrorDTO> analysisErrors = new ArrayList<>();
        List<IpAnalysisResponseDTO> analyses = new ArrayList<>();
        Set<String> processedInRequest = new HashSet<>();

        for (IpAddressImportItemDTO item : items) {
            String address = item.getAddress();

            try {
                if (!processedInRequest.add(address)) {
                    duplicated.add(address);
                    continue;
                }

                IpValidator.validate(address);

                if (ipAddressRepository.findByAddress(address).isPresent()) {
                    duplicated.add(address);
                    continue;
                }

                IpAddress ipAddress = IpAddress.builder()
                        .address(address)
                        .description(item.getDescription())
                        .build();

                IpAddress savedIpAddress = ipAddressRepository.save(ipAddress);

                IpAddressResponseDTO importedIpAddress = ipAddressService.toResponseDTO(savedIpAddress);
                imported.add(importedIpAddress);

                if (Boolean.TRUE.equals(analyzeAfterImport)) {
                    try {
                        analyses.add(ipAnalysisService.analyze(address));
                    } catch (Exception exception) {
                        analysisErrors.add(IpAddressImportAnalysisErrorDTO.builder()
                                .address(address)
                                .message(exception.getMessage())
                                .build());
                    }
                }
            } catch (Exception exception) {
                importErrors.add(IpAddressImportErrorDTO.builder()
                        .address(address)
                        .message(exception.getMessage())
                        .build());
            }
        }

        long lowRiskCount = countAnalysesByRiskLevel(analyses, RiskLevel.LOW);
        long attentionRiskCount = countAnalysesByRiskLevel(analyses, RiskLevel.ATTENTION);
        long mediumRiskCount = countAnalysesByRiskLevel(analyses, RiskLevel.MEDIUM);
        long highRiskCount = countAnalysesByRiskLevel(analyses, RiskLevel.HIGH);
        long criticalRiskCount = countAnalysesByRiskLevel(analyses, RiskLevel.CRITICAL);

        return IpAddressImportResponseDTO.builder()
                .totalReceived(items.size())
                .importedCount(imported.size())
                .duplicatedCount(duplicated.size())
                .errorCount(importErrors.size() + analysisErrors.size())
                .analysisCount(analyses.size())
                .lowRiskCount(lowRiskCount)
                .attentionRiskCount(attentionRiskCount)
                .mediumRiskCount(mediumRiskCount)
                .highRiskCount(highRiskCount)
                .criticalRiskCount(criticalRiskCount)
                .imported(imported)
                .duplicated(duplicated)
                .errors(importErrors)
                .importErrors(importErrors)
                .analysisErrors(analysisErrors)
                .analyses(analyses)
                .build();
    }

    private long countAnalysesByRiskLevel(
            List<IpAnalysisResponseDTO> analyses,
            RiskLevel riskLevel
    ) {
        return analyses.stream()
                .filter(analysis -> analysis.getRiskLevel() == riskLevel)
                .count();
    }
}