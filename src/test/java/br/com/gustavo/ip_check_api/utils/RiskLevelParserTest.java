package br.com.gustavo.ip_check_api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import br.com.gustavo.ip_check_api.enums.RiskLevel;

class RiskLevelParserTest {

    @Test
    void shouldParseUppercaseRiskLevel() {
        RiskLevel result = RiskLevelParser.parse("CRITICAL");

        assertEquals(RiskLevel.CRITICAL, result);
    }

    @Test
    void shouldParseLowercaseRiskLevel() {
        RiskLevel result = RiskLevelParser.parse("critical");

        assertEquals(RiskLevel.CRITICAL, result);
    }

    @Test
    void shouldParseRiskLevelWithSpaces() {
        RiskLevel result = RiskLevelParser.parse(" medium ");

        assertEquals(RiskLevel.MEDIUM, result);
    }

    @Test
    void shouldRejectInvalidRiskLevel() {
        assertThrows(IllegalArgumentException.class, () -> RiskLevelParser.parse("invalid"));
    }

    @Test
    void shouldRejectBlankRiskLevel() {
        assertThrows(IllegalArgumentException.class, () -> RiskLevelParser.parse(" "));
    }
}