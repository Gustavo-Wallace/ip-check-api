package br.com.gustavo.ip_check_api.dtos;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IpAddressResponseDTO {

    private Long id;
    private String address;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
}