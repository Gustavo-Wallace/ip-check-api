package br.com.gustavo.ip_check_api.services;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.gustavo.ip_check_api.clients.IpIntelligenceClient;
import br.com.gustavo.ip_check_api.clients.IpIntelligenceClientFactory;
import br.com.gustavo.ip_check_api.config.IpIntelligenceProperties;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.enums.AnalysisSource;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.exceptions.ExternalIpIntelligenceException;
import br.com.gustavo.ip_check_api.models.IpAnalysis;
import br.com.gustavo.ip_check_api.repositories.IpAddressRepository;
import br.com.gustavo.ip_check_api.repositories.IpAnalysisRepository;

class IpAnalysisServiceTest {

    private IpAnalysisRepository ipAnalysisRepository;
    private IpIntelligenceClientFactory ipIntelligenceClientFactory;
    private IpAddressRepository ipAddressRepository;
    private IpIntelligenceProperties ipIntelligenceProperties;
    private IpAnalysisService ipAnalysisService;

    @BeforeEach
    void setUp() {
        ipAnalysisRepository = Mockito.mock(IpAnalysisRepository.class);
        ipIntelligenceClientFactory = Mockito.mock(IpIntelligenceClientFactory.class);
        ipAddressRepository = Mockito.mock(IpAddressRepository.class);
        ipIntelligenceProperties = Mockito.mock(IpIntelligenceProperties.class);

        ipAnalysisService = new IpAnalysisService(
                ipAnalysisRepository,
                ipIntelligenceClientFactory,
                ipAddressRepository,
                ipIntelligenceProperties);
    }

    @Test
    void shouldReturnRecentAnalysisFromCache() {
        IpAnalysis recentAnalysis = IpAnalysis.builder()
                .id(1L)
                .address("8.8.8.8")
                .vpn(false)
                .proxy(false)
                .tor(false)
                .datacenter(false)
                .anonymous(false)
                .riskLevel(RiskLevel.LOW)
                .source(AnalysisSource.EXTERNAL_API)
                .externalRiskScore(0)
                .externalType("Business")
                .externalProvider("Google LLC")
                .asn("AS15169")
                .country("United States")
                .city("Mountain View")
                .hostname("dns.google")
                .networkRange("8.8.8.0/24")
                .analyzedAt(LocalDateTime.now())
                .build();

        when(ipIntelligenceProperties.getCacheDurationMinutes())
                .thenReturn(60L);

        when(ipAnalysisRepository.findTopByAddressOrderByAnalyzedAtDesc("8.8.8.8"))
                .thenReturn(Optional.of(recentAnalysis));

        IpAnalysisResponseDTO response = ipAnalysisService.analyze("8.8.8.8");

        assertEquals(1L, response.getId());
        assertEquals("8.8.8.8", response.getAddress());
        assertEquals(RiskLevel.LOW, response.getRiskLevel());
        assertEquals("Google LLC", response.getExternalProvider());

        verify(ipIntelligenceClientFactory, never()).getClient();
        verify(ipAnalysisRepository, never()).save(any(IpAnalysis.class));
    }

    @Test
    void shouldThrowExceptionWhenExternalClientFails() {
        IpIntelligenceClient ipIntelligenceClient = Mockito.mock(IpIntelligenceClient.class);

        when(ipIntelligenceProperties.getCacheDurationMinutes())
                .thenReturn(60L);

        when(ipAnalysisRepository.findTopByAddressOrderByAnalyzedAtDesc("8.8.8.8"))
                .thenReturn(Optional.empty());

        when(ipIntelligenceClientFactory.getClient())
                .thenReturn(ipIntelligenceClient);

        when(ipIntelligenceClient.check("8.8.8.8"))
                .thenThrow(new ExternalIpIntelligenceException("Failed to check IP using ProxyCheck"));

        ExternalIpIntelligenceException exception = assertThrows(
                ExternalIpIntelligenceException.class,
                () -> ipAnalysisService.analyze("8.8.8.8"));

        assertEquals("Failed to check IP using ProxyCheck", exception.getMessage());

        verify(ipIntelligenceClientFactory, times(1)).getClient();
        verify(ipIntelligenceClient, times(1)).check("8.8.8.8");
        verify(ipAnalysisRepository, never()).save(any(IpAnalysis.class));
    }
}