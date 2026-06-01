package br.com.gustavo.ip_check_api.services;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.gustavo.ip_check_api.config.IpImportProperties;
import br.com.gustavo.ip_check_api.dtos.IpAddressCsvImportRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressImportAnalysisErrorDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressImportErrorDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressImportItemDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressImportRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressImportResponseDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressResponseDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.exceptions.ResourceNotFoundException;
import br.com.gustavo.ip_check_api.models.IpAddress;
import br.com.gustavo.ip_check_api.repositories.IpAddressRepository;
import br.com.gustavo.ip_check_api.utils.IpValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpAddressService {

    private final IpAddressRepository ipAddressRepository;
    private final IpAnalysisService ipAnalysisService;
    private final IpImportProperties ipImportProperties;

    public IpAddressResponseDTO create(IpAddressRequestDTO requestDTO) {
        IpValidator.validate(requestDTO.getAddress());

        if (ipAddressRepository.findByAddress(requestDTO.getAddress()).isPresent()) {
            throw new IllegalArgumentException("IP address already registered");
        }

        IpAddress ipAddress = IpAddress.builder()
                .address(requestDTO.getAddress())
                .description(requestDTO.getDescription())
                .build();

        IpAddress savedIpAddress = ipAddressRepository.save(ipAddress);

        return toResponseDTO(savedIpAddress);
    }

    public List<IpAddressResponseDTO> findAll() {
        return ipAddressRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private IpAddressResponseDTO toResponseDTO(IpAddress ipAddress) {
        return IpAddressResponseDTO.builder()
                .id(ipAddress.getId())
                .address(ipAddress.getAddress())
                .description(ipAddress.getDescription())
                .active(ipAddress.getActive())
                .createdAt(ipAddress.getCreatedAt())
                .build();
    }

    public IpAddressResponseDTO findById(Long id) {
        IpAddress ipAddress = ipAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IP address not found"));

        return toResponseDTO(ipAddress);
    }

    public IpAddressResponseDTO deactivate(Long id) {
        IpAddress ipAddress = ipAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IP address not found"));

        ipAddress.setActive(false);

        IpAddress updatedIpAddress = ipAddressRepository.save(ipAddress);

        return toResponseDTO(updatedIpAddress);
    }

    public IpAddressResponseDTO activate(Long id) {
        IpAddress ipAddress = ipAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IP address not found"));

        ipAddress.setActive(true);

        IpAddress updatedIpAddress = ipAddressRepository.save(ipAddress);

        return toResponseDTO(updatedIpAddress);
    }

    public List<IpAddressResponseDTO> findAllActive() {
        return ipAddressRepository.findByActiveTrue()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Page<IpAddressResponseDTO> findAllPaged(Pageable pageable) {
        return ipAddressRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

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
        List<IpAddressImportItemDTO> items = new ArrayList<>();

        String[] lines = requestDTO.getCsvContent().split("\\R");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isBlank()) {
                continue;
            }

            if (i == 0 && line.toLowerCase().contains("address")) {
                continue;
            }

            String[] columns = line.split(",", -1);

            if (columns.length == 0) {
                continue;
            }

            String address = columns[0].trim();

            if (address.isBlank()) {
                continue;
            }

            String description = null;

            if (columns.length > 1 && !columns[1].trim().isBlank()) {
                description = columns[1].trim();
            }

            items.add(IpAddressImportItemDTO.builder()
                    .address(address)
                    .description(description != null ? description : "Imported from CSV text")
                    .build());
        }

        return importIpAddressItems(items, requestDTO.getAnalyzeAfterImport());
    }

    public IpAddressImportResponseDTO importIpAddressesFromCsvFile(
            MultipartFile file,
            Boolean analyzeAfterImport) {
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
            Boolean analyzeAfterImport) {
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

                IpAddressResponseDTO importedIpAddress = toResponseDTO(savedIpAddress);
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

        return IpAddressImportResponseDTO.builder()
                .totalReceived(items.size())
                .importedCount(imported.size())
                .duplicatedCount(duplicated.size())
                .errorCount(importErrors.size() + analysisErrors.size())
                .imported(imported)
                .duplicated(duplicated)
                .errors(importErrors)
                .importErrors(importErrors)
                .analysisErrors(analysisErrors)
                .analyses(analyses)
                .build();
    }

}