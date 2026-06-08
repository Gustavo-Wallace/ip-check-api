package br.com.gustavo.ip_check_api.controllers;

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
}