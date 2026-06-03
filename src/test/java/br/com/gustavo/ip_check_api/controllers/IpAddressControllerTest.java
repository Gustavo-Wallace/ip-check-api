package br.com.gustavo.ip_check_api.controllers;

import java.time.LocalDateTime;
import java.util.List;

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
import br.com.gustavo.ip_check_api.dtos.IpAddressResponseDTO;
import br.com.gustavo.ip_check_api.services.IpAddressImportService;
import br.com.gustavo.ip_check_api.services.IpAddressService;

@WebMvcTest(IpAddressController.class)
class IpAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IpAddressService ipAddressService;

    @MockitoBean
    private IpAddressImportService ipAddressImportService;

    @MockitoBean
    private IpIntelligenceProperties ipIntelligenceProperties;

    @MockitoBean
    private IpImportProperties ipImportProperties;

    @Test
    void shouldListIpAddresses() throws Exception {
        List<IpAddressResponseDTO> ipAddresses = List.of(
                IpAddressResponseDTO.builder()
                        .id(1L)
                        .address("8.8.8.8")
                        .description("Google DNS")
                        .active(true)
                        .createdAt(LocalDateTime.of(2026, 6, 2, 15, 0))
                        .build(),
                IpAddressResponseDTO.builder()
                        .id(2L)
                        .address("1.1.1.1")
                        .description("Cloudflare DNS")
                        .active(true)
                        .createdAt(LocalDateTime.of(2026, 6, 2, 15, 5))
                        .build());

        when(ipAddressService.findAll()).thenReturn(ipAddresses);

        mockMvc.perform(get("/ips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].address").value("8.8.8.8"))
                .andExpect(jsonPath("$[0].description").value("Google DNS"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].address").value("1.1.1.1"))
                .andExpect(jsonPath("$[1].description").value("Cloudflare DNS"))
                .andExpect(jsonPath("$[1].active").value(true));
    }
}