package br.com.gustavo.ip_check_api.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.gustavo.ip_check_api.config.IpImportProperties;
import br.com.gustavo.ip_check_api.dtos.IpAddressImportRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressImportResponseDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressResponseDTO;
import br.com.gustavo.ip_check_api.models.IpAddress;
import br.com.gustavo.ip_check_api.repositories.IpAddressRepository;

class IpAddressImportServiceTest {

    private IpAddressRepository ipAddressRepository;
    private IpAnalysisService ipAnalysisService;
    private IpImportProperties ipImportProperties;
    private IpAddressService ipAddressService;
    private IpAddressImportService ipAddressImportService;

    @BeforeEach
    void setUp() {
        ipAddressRepository = Mockito.mock(IpAddressRepository.class);
        ipAnalysisService = Mockito.mock(IpAnalysisService.class);
        ipImportProperties = Mockito.mock(IpImportProperties.class);
        ipAddressService = Mockito.mock(IpAddressService.class);

        ipAddressImportService = new IpAddressImportService(
                ipAddressRepository,
                ipAnalysisService,
                ipImportProperties,
                ipAddressService);
    }

    @Test
    void shouldImportValidIpAddresses() {
        IpAddressImportRequestDTO requestDTO = new IpAddressImportRequestDTO();
        requestDTO.setAddresses(List.of("8.8.8.8", "1.1.1.1"));
        requestDTO.setDescription("Imported test");
        requestDTO.setAnalyzeAfterImport(false);

        when(ipAddressRepository.findByAddress(anyString()))
                .thenReturn(Optional.empty());

        when(ipAddressRepository.save(any(IpAddress.class)))
                .thenAnswer(invocation -> {
                    IpAddress ipAddress = invocation.getArgument(0);
                    ipAddress.setId(ipAddress.getAddress().equals("8.8.8.8") ? 1L : 2L);
                    ipAddress.setActive(true);
                    ipAddress.setCreatedAt(LocalDateTime.now());
                    return ipAddress;
                });

        when(ipAddressService.toResponseDTO(any(IpAddress.class)))
                .thenAnswer(invocation -> {
                    IpAddress ipAddress = invocation.getArgument(0);

                    return IpAddressResponseDTO.builder()
                            .id(ipAddress.getId())
                            .address(ipAddress.getAddress())
                            .description(ipAddress.getDescription())
                            .active(ipAddress.getActive())
                            .createdAt(ipAddress.getCreatedAt())
                            .build();
                });

        IpAddressImportResponseDTO response = ipAddressImportService.importIpAddresses(requestDTO);

        assertEquals(2, response.getTotalReceived());
        assertEquals(2, response.getImportedCount());
        assertEquals(0, response.getDuplicatedCount());
        assertEquals(0, response.getErrorCount());
        assertEquals(2, response.getImported().size());

        verify(ipAddressRepository, times(2)).save(any(IpAddress.class));
        verify(ipAnalysisService, never()).analyze(anyString());
    }
}