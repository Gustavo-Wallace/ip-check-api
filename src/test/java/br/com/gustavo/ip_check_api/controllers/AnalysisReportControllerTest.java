package br.com.gustavo.ip_check_api.controllers;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.gustavo.ip_check_api.config.IpImportProperties;
import br.com.gustavo.ip_check_api.config.IpIntelligenceProperties;
import br.com.gustavo.ip_check_api.dtos.AnalysisSummaryReportDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.enums.AnalysisSource;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.services.IpAnalysisService;

@WebMvcTest(AnalysisReportController.class)
class AnalysisReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IpAnalysisService ipAnalysisService;

    @MockitoBean
    private IpIntelligenceProperties ipIntelligenceProperties;

    @MockitoBean
    private IpImportProperties ipImportProperties;

    @Test
    void shouldReturnAnalysisSummaryReport() throws Exception {
        AnalysisSummaryReportDTO summaryReport = AnalysisSummaryReportDTO.builder()
                .totalAnalyses(10L)
                .anonymousCount(3L)
                .vpnCount(1L)
                .proxyCount(2L)
                .torCount(1L)
                .datacenterCount(4L)
                .highestRiskLevel(RiskLevel.CRITICAL)
                .build();

        when(ipAnalysisService.getSummaryReport()).thenReturn(summaryReport);

        mockMvc.perform(get("/analyses/report/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAnalyses").value(10))
                .andExpect(jsonPath("$.anonymousCount").value(3))
                .andExpect(jsonPath("$.vpnCount").value(1))
                .andExpect(jsonPath("$.proxyCount").value(2))
                .andExpect(jsonPath("$.torCount").value(1))
                .andExpect(jsonPath("$.datacenterCount").value(4))
                .andExpect(jsonPath("$.highestRiskLevel").value("CRITICAL"));
    }

    @Test
    void shouldReturnRiskLevelReport() throws Exception {
        Map<RiskLevel, Long> riskLevelReport = Map.of(
                RiskLevel.LOW, 4L,
                RiskLevel.ATTENTION, 2L,
                RiskLevel.MEDIUM, 2L,
                RiskLevel.HIGH, 1L,
                RiskLevel.CRITICAL, 1L);

        when(ipAnalysisService.countByRiskLevel()).thenReturn(riskLevelReport);

        mockMvc.perform(get("/analyses/report/risk-level"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.LOW").value(4))
                .andExpect(jsonPath("$.ATTENTION").value(2))
                .andExpect(jsonPath("$.MEDIUM").value(2))
                .andExpect(jsonPath("$.HIGH").value(1))
                .andExpect(jsonPath("$.CRITICAL").value(1));
    }

    @Test
    void shouldReturnAnonymityReport() throws Exception {
        Map<String, Long> anonymityReport = Map.of(
                "anonymous", 3L,
                "vpn", 1L,
                "proxy", 2L,
                "tor", 1L,
                "datacenter", 4L);

        when(ipAnalysisService.countByAnonymityIndicators()).thenReturn(anonymityReport);

        mockMvc.perform(get("/analyses/report/anonymity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anonymous").value(3))
                .andExpect(jsonPath("$.vpn").value(1))
                .andExpect(jsonPath("$.proxy").value(2))
                .andExpect(jsonPath("$.tor").value(1))
                .andExpect(jsonPath("$.datacenter").value(4));
    }

    @Test
    void shouldFindAnalysesByRiskLevel() throws Exception {
        List<IpAnalysisResponseDTO> analyses = List.of(
                IpAnalysisResponseDTO.builder()
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
                        .analyzedAt(LocalDateTime.of(2026, 6, 2, 15, 0))
                        .build());

        when(ipAnalysisService.findByRiskLevel(RiskLevel.LOW)).thenReturn(analyses);

        mockMvc.perform(get("/analyses/risk-level/low"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].address").value("8.8.8.8"))
                .andExpect(jsonPath("$[0].vpn").value(false))
                .andExpect(jsonPath("$[0].proxy").value(false))
                .andExpect(jsonPath("$[0].tor").value(false))
                .andExpect(jsonPath("$[0].datacenter").value(false))
                .andExpect(jsonPath("$[0].anonymous").value(false))
                .andExpect(jsonPath("$[0].riskLevel").value("LOW"))
                .andExpect(jsonPath("$[0].source").value("EXTERNAL_API"))
                .andExpect(jsonPath("$[0].externalRiskScore").value(0))
                .andExpect(jsonPath("$[0].externalType").value("Business"))
                .andExpect(jsonPath("$[0].externalProvider").value("Google LLC"))
                .andExpect(jsonPath("$[0].asn").value("AS15169"))
                .andExpect(jsonPath("$[0].country").value("United States"))
                .andExpect(jsonPath("$[0].city").value("Mountain View"))
                .andExpect(jsonPath("$[0].hostname").value("dns.google"))
                .andExpect(jsonPath("$[0].networkRange").value("8.8.8.0/24"));
    }

    @Test
    void shouldReturnBadRequestWhenRiskLevelIsInvalid() throws Exception {
        mockMvc.perform(get("/analyses/risk-level/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFilterAnalysesByIndicators() throws Exception {
        List<IpAnalysisResponseDTO> analyses = List.of(
                IpAnalysisResponseDTO.builder()
                        .id(1L)
                        .address("45.90.28.1")
                        .vpn(true)
                        .proxy(false)
                        .tor(false)
                        .datacenter(false)
                        .anonymous(true)
                        .riskLevel(RiskLevel.MEDIUM)
                        .source(AnalysisSource.EXTERNAL_API)
                        .externalRiskScore(50)
                        .externalType("VPN")
                        .externalProvider("Example VPN Provider")
                        .asn("AS12345")
                        .country("Brazil")
                        .city("Brasilia")
                        .hostname("vpn.example.com")
                        .networkRange("45.90.28.0/24")
                        .analyzedAt(LocalDateTime.of(2026, 6, 2, 15, 0))
                        .build());

        when(ipAnalysisService.filterByIndicators(true, false, false, false, false))
                .thenReturn(analyses);

        mockMvc.perform(get("/analyses/filter")
                .param("vpn", "true")
                .param("proxy", "false")
                .param("tor", "false")
                .param("datacenter", "false")
                .param("anonymous", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].address").value("45.90.28.1"))
                .andExpect(jsonPath("$[0].vpn").value(true))
                .andExpect(jsonPath("$[0].proxy").value(false))
                .andExpect(jsonPath("$[0].tor").value(false))
                .andExpect(jsonPath("$[0].datacenter").value(false))
                .andExpect(jsonPath("$[0].anonymous").value(true))
                .andExpect(jsonPath("$[0].riskLevel").value("MEDIUM"))
                .andExpect(jsonPath("$[0].source").value("EXTERNAL_API"))
                .andExpect(jsonPath("$[0].externalProvider").value("Example VPN Provider"));
    }

    @Test
    void shouldFilterAnalysesWithoutIndicators() throws Exception {
        when(ipAnalysisService.filterByIndicators(null, null, null, null, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/analyses/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

}