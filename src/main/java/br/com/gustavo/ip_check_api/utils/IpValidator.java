package br.com.gustavo.ip_check_api.utils;

import java.net.InetAddress;

public class IpValidator {

    private IpValidator() {
    }

    public static void validate(String address) {
        try {
            InetAddress.getByName(address);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid IP address");
        }
    }
}