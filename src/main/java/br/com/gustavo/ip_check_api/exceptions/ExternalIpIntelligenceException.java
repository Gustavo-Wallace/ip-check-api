package br.com.gustavo.ip_check_api.exceptions;

public class ExternalIpIntelligenceException extends RuntimeException {

    public ExternalIpIntelligenceException(String message) {
        super(message);
    }

    public ExternalIpIntelligenceException(String message, Throwable cause) {
        super(message, cause);
    }
}