package br.com.gustavo.ip_check_api.controllers;

import br.com.gustavo.ip_check_api.config.IpImportProperties;
import br.com.gustavo.ip_check_api.config.IpIntelligenceProperties;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisManualRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAnalysisResponseDTO;
import br.com.gustavo.ip_check_api.enums.AnalysisSource;
import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.services.IpAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IpAnalysisController.class)
class IpAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IpAnalysisService ipAnalysisService;

    @MockitoBean
    private IpIntelligenceProperties ipIntelligenceProperties;

    @MockitoBean
    private IpImportProperties ipImportProperties;

    @Test
    void shouldAnalyzeIpAddress() throws Exception {
        IpAnalysisResponseDTO responseDTO = IpAnalysisResponseDTO.builder()
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
                .build();

        when(ipAnalysisService.analyze("8.8.8.8")).thenReturn(responseDTO);

        mockMvc.perform(post("/ips/8.8.8.8/analyze"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("8.8.8.8"))
                .andExpect(jsonPath("$.vpn").value(false))
                .andExpect(jsonPath("$.proxy").value(false))
                .andExpect(jsonPath("$.tor").value(false))
                .andExpect(jsonPath("$.datacenter").value(false))
                .andExpect(jsonPath("$.anonymous").value(false))
                .andExpect(jsonPath("$.riskLevel").value("LOW"))
                .andExpect(jsonPath("$.source").value("EXTERNAL_API"))
                .andExpect(jsonPath("$.externalRiskScore").value(0))
                .andExpect(jsonPath("$.externalType").value("Business"))
                .andExpect(jsonPath("$.externalProvider").value("Google LLC"))
                .andExpect(jsonPath("$.asn").value("AS15169"))
                .andExpect(jsonPath("$.country").value("United States"))
                .andExpect(jsonPath("$.city").value("Mountain View"))
                .andExpect(jsonPath("$.hostname").value("dns.google"))
                .andExpect(jsonPath("$.networkRange").value("8.8.8.0/24"));
    }

    @Test
    void shouldAnalyzeIpAddressManually() throws Exception {
        IpAnalysisResponseDTO responseDTO = IpAnalysisResponseDTO.builder()
                .id(1L)
                .address("45.90.28.1")
                .vpn(true)
                .proxy(false)
                .tor(false)
                .datacenter(false)
                .anonymous(true)
                .riskLevel(RiskLevel.MEDIUM)
                .source(AnalysisSource.MANUAL_SIMULATION)
                .externalRiskScore(null)
                .externalType("Manual")
                .externalProvider("Manual input")
                .asn(null)
                .country(null)
                .city(null)
                .hostname(null)
                .networkRange(null)
                .analyzedAt(LocalDateTime.of(2026, 6, 2, 15, 0))
                .build();

        when(ipAnalysisService.analyzeManually(
                org.mockito.ArgumentMatchers.eq("45.90.28.1"),
                any(IpAnalysisManualRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/ips/45.90.28.1/analyze/manual")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "vpn": true,
                          "proxy": false,
                          "tor": false,
                          "datacenter": false,
                          "anonymous": true,
                          "riskLevel": "MEDIUM"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("45.90.28.1"))
                .andExpect(jsonPath("$.vpn").value(true))
                .andExpect(jsonPath("$.proxy").value(false))
                .andExpect(jsonPath("$.tor").value(false))
                .andExpect(jsonPath("$.datacenter").value(false))
                .andExpect(jsonPath("$.anonymous").value(true))
                .andExpect(jsonPath("$.riskLevel").value("MEDIUM"))
                .andExpect(jsonPath("$.source").value("MANUAL_SIMULATION"))
                .andExpect(jsonPath("$.externalType").value("Manual"))
                .andExpect(jsonPath("$.externalProvider").value("Manual input"));
    }
}