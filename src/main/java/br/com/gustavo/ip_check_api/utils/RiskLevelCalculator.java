package br.com.gustavo.ip_check_api.utils;

import br.com.gustavo.ip_check_api.enums.RiskLevel;

public class RiskLevelCalculator {

    private RiskLevelCalculator() {
    }

    public static RiskLevel calculate(
            Boolean vpn,
            Boolean proxy,
            Boolean tor,
            Boolean datacenter,
            Boolean anonymous,
            Integer externalRiskScore) {
        if (Boolean.TRUE.equals(tor)) {
            return RiskLevel.CRITICAL;
        }

        if (externalRiskScore != null && externalRiskScore >= 90) {
            return RiskLevel.CRITICAL;
        }

        if (Boolean.TRUE.equals(anonymous) && Boolean.TRUE.equals(proxy)) {
            return RiskLevel.HIGH;
        }

        if (Boolean.TRUE.equals(vpn) && Boolean.TRUE.equals(proxy)) {
            return RiskLevel.HIGH;
        }

        if (externalRiskScore != null && externalRiskScore >= 70) {
            return RiskLevel.HIGH;
        }

        if (Boolean.TRUE.equals(vpn) || Boolean.TRUE.equals(proxy)) {
            return RiskLevel.MEDIUM;
        }

        if (externalRiskScore != null && externalRiskScore >= 40) {
            return RiskLevel.MEDIUM;
        }

        if (Boolean.TRUE.equals(datacenter)) {
            return RiskLevel.ATTENTION;
        }

        if (externalRiskScore != null && externalRiskScore >= 20) {
            return RiskLevel.ATTENTION;
        }

        return RiskLevel.LOW;
    }

    public static int getRiskWeight(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case LOW -> 1;
            case ATTENTION -> 2;
            case MEDIUM -> 3;
            case HIGH -> 4;
            case CRITICAL -> 5;
        };
    }

    public static int compare(RiskLevel first, RiskLevel second) {
        return Integer.compare(getRiskWeight(first), getRiskWeight(second));
    }
}