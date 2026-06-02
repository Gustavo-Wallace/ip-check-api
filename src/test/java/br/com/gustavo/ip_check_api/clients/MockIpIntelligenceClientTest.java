package br.com.gustavo.ip_check_api.clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import br.com.gustavo.ip_check_api.dtos.ExternalIpCheckResponseDTO;

class MockIpIntelligenceClientTest {

    private final MockIpIntelligenceClient client = new MockIpIntelligenceClient();

    @Test
    void shouldReturnDatacenterTrueForKnownDnsIp() {
        ExternalIpCheckResponseDTO result = client.check("8.8.8.8");

        assertEquals("8.8.8.8", result.getAddress());
        assertFalse(result.getVpn());
        assertFalse(result.getProxy());
        assertFalse(result.getTor());
        assertTrue(result.getDatacenter());
        assertEquals(0, result.getExternalRiskScore());
        assertEquals("MOCK", result.getExternalType());
        assertEquals("Mock Provider", result.getExternalProvider());
        assertEquals("MOCK-ASN", result.getAsn());
        assertEquals("Mock Country", result.getCountry());
        assertEquals("Mock City", result.getCity());
        assertEquals("mock.local", result.getHostname());
        assertEquals("0.0.0.0/0", result.getNetworkRange());
    }

    @Test
    void shouldReturnDatacenterFalseForUnknownIp() {
        ExternalIpCheckResponseDTO result = client.check("187.45.193.134");

        assertEquals("187.45.193.134", result.getAddress());
        assertFalse(result.getVpn());
        assertFalse(result.getProxy());
        assertFalse(result.getTor());
        assertFalse(result.getDatacenter());
        assertEquals(0, result.getExternalRiskScore());
        assertEquals("MOCK", result.getExternalType());
        assertEquals("Mock Provider", result.getExternalProvider());
    }
}