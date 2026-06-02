package br.com.gustavo.ip_check_api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import br.com.gustavo.ip_check_api.enums.RiskLevel;

class RiskLevelCalculatorTest {

    @Test
    void shouldReturnCriticalWhenTorIsTrue() {
        RiskLevel result = RiskLevelCalculator.calculate(
                false,
                false,
                true,
                false,
                true,
                0
        );

        assertEquals(RiskLevel.CRITICAL, result);
    }

    @Test
    void shouldReturnCriticalWhenExternalRiskScoreIsAtLeast90() {
        RiskLevel result = RiskLevelCalculator.calculate(
                false,
                false,
                false,
                false,
                false,
                90
        );

        assertEquals(RiskLevel.CRITICAL, result);
    }

    @Test
    void shouldReturnHighWhenVpnAndProxyAreTrue() {
        RiskLevel result = RiskLevelCalculator.calculate(
                true,
                true,
                false,
                false,
                true,
                0
        );

        assertEquals(RiskLevel.HIGH, result);
    }

    @Test
    void shouldReturnHighWhenExternalRiskScoreIsAtLeast70() {
        RiskLevel result = RiskLevelCalculator.calculate(
                false,
                false,
                false,
                false,
                false,
                70
        );

        assertEquals(RiskLevel.HIGH, result);
    }

    @Test
    void shouldReturnMediumWhenVpnIsTrue() {
        RiskLevel result = RiskLevelCalculator.calculate(
                true,
                false,
                false,
                false,
                true,
                0
        );

        assertEquals(RiskLevel.MEDIUM, result);
    }

    @Test
    void shouldReturnMediumWhenProxyIsTrue() {
        RiskLevel result = RiskLevelCalculator.calculate(
                false,
                true,
                false,
                false,
                true,
                0
        );

        assertEquals(RiskLevel.MEDIUM, result);
    }

    @Test
    void shouldReturnMediumWhenExternalRiskScoreIsAtLeast40() {
        RiskLevel result = RiskLevelCalculator.calculate(
                false,
                false,
                false,
                false,
                false,
                40
        );

        assertEquals(RiskLevel.MEDIUM, result);
    }

    @Test
    void shouldReturnAttentionWhenDatacenterIsTrue() {
        RiskLevel result = RiskLevelCalculator.calculate(
                false,
                false,
                false,
                true,
                false,
                0
        );

        assertEquals(RiskLevel.ATTENTION, result);
    }

    @Test
    void shouldReturnAttentionWhenExternalRiskScoreIsAtLeast20() {
        RiskLevel result = RiskLevelCalculator.calculate(
                false,
                false,
                false,
                false,
                false,
                20
        );

        assertEquals(RiskLevel.ATTENTION, result);
    }

    @Test
    void shouldReturnLowWhenNoIndicatorIsDetected() {
        RiskLevel result = RiskLevelCalculator.calculate(
                false,
                false,
                false,
                false,
                false,
                0
        );

        assertEquals(RiskLevel.LOW, result);
    }

    @Test
    void shouldReturnLowWhenAllValuesAreNull() {
        RiskLevel result = RiskLevelCalculator.calculate(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertEquals(RiskLevel.LOW, result);
    }

    @Test
    void shouldCompareRiskLevelsByWeight() {
        int result = RiskLevelCalculator.compare(RiskLevel.LOW, RiskLevel.CRITICAL);

        assertEquals(-1, result);
    }
}