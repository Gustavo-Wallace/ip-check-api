package br.com.gustavo.ip_check_api.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class IpValidatorTest {

    @Test
    void shouldAcceptValidIpv4Address() {
        assertDoesNotThrow(() -> IpValidator.validate("8.8.8.8"));
    }

    @Test
    void shouldAcceptValidIpv6Address() {
        assertDoesNotThrow(() -> IpValidator.validate("2001:4860:4860::8888"));
    }

    @Test
    void shouldRejectInvalidIpAddress() {
        assertThrows(IllegalArgumentException.class, () -> IpValidator.validate("999.999.999.999"));
    }

    @Test
    void shouldRejectTextAsIpAddress() {
        assertThrows(IllegalArgumentException.class, () -> IpValidator.validate("not-an-ip"));
    }
}