package br.com.gustavo.ip_check_api.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.gustavo.ip_check_api.config.IpImportProperties;
import br.com.gustavo.ip_check_api.config.IpIntelligenceProperties;
import br.com.gustavo.ip_check_api.dtos.IpAddressRequestDTO;
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

    @Test
    void shouldListActiveIpAddresses() throws Exception {
        List<IpAddressResponseDTO> activeIpAddresses = List.of(
                IpAddressResponseDTO.builder()
                        .id(1L)
                        .address("8.8.8.8")
                        .description("Google DNS")
                        .active(true)
                        .createdAt(LocalDateTime.of(2026, 6, 2, 15, 0))
                        .build());

        when(ipAddressService.findAllActive()).thenReturn(activeIpAddresses);

        mockMvc.perform(get("/ips/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].address").value("8.8.8.8"))
                .andExpect(jsonPath("$[0].description").value("Google DNS"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void shouldFindIpAddressById() throws Exception {
        IpAddressResponseDTO ipAddress = IpAddressResponseDTO.builder()
                .id(1L)
                .address("8.8.8.8")
                .description("Google DNS")
                .active(true)
                .createdAt(LocalDateTime.of(2026, 6, 2, 15, 0))
                .build();

        when(ipAddressService.findById(1L)).thenReturn(ipAddress);

        mockMvc.perform(get("/ips/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("8.8.8.8"))
                .andExpect(jsonPath("$.description").value("Google DNS"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldCreateIpAddress() throws Exception {
        IpAddressResponseDTO createdIpAddress = IpAddressResponseDTO.builder()
                .id(1L)
                .address("8.8.8.8")
                .description("Google DNS")
                .active(true)
                .createdAt(LocalDateTime.of(2026, 6, 2, 15, 0))
                .build();

        when(ipAddressService.create(any(IpAddressRequestDTO.class)))
                .thenReturn(createdIpAddress);

        mockMvc.perform(post("/ips")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "address": "8.8.8.8",
                          "description": "Google DNS"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("8.8.8.8"))
                .andExpect(jsonPath("$.description").value("Google DNS"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingIpAddressWithoutAddress() throws Exception {
        mockMvc.perform(post("/ips")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "description": "Missing address"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}