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

@WebMvcTest(IpIntelligenceConfigController.class)
class IpIntelligenceConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IpIntelligenceProperties ipIntelligenceProperties;

    @MockitoBean
    private IpImportProperties ipImportProperties;

    @Test
    void shouldReturnIpIntelligenceConfig() throws Exception {
        when(ipIntelligenceProperties.getProvider()).thenReturn("proxycheck");
        when(ipIntelligenceProperties.getBaseUrl()).thenReturn("https://proxycheck.io/v2");
        when(ipIntelligenceProperties.getApiKey()).thenReturn("test-api-key");
        when(ipIntelligenceProperties.getCacheDurationMinutes()).thenReturn(60L);

        mockMvc.perform(get("/ip-intelligence/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("proxycheck"))
                .andExpect(jsonPath("$.baseUrlConfigured").value(true))
                .andExpect(jsonPath("$.apiKeyConfigured").value(true))
                .andExpect(jsonPath("$.cacheDurationMinutes").value(60));
    }

    @Test
    void shouldReturnFalseWhenApiKeyIsNotConfigured() throws Exception {
        when(ipIntelligenceProperties.getProvider()).thenReturn("mock");
        when(ipIntelligenceProperties.getBaseUrl()).thenReturn("https://proxycheck.io/v2");
        when(ipIntelligenceProperties.getApiKey()).thenReturn("");
        when(ipIntelligenceProperties.getCacheDurationMinutes()).thenReturn(60L);

        mockMvc.perform(get("/ip-intelligence/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("mock"))
                .andExpect(jsonPath("$.baseUrlConfigured").value(true))
                .andExpect(jsonPath("$.apiKeyConfigured").value(false))
                .andExpect(jsonPath("$.cacheDurationMinutes").value(60));
    }
}