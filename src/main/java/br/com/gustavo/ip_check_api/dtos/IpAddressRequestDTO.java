package br.com.gustavo.ip_check_api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpAddressRequestDTO {

    @NotBlank(message = "IP address is required")
    @Size(max = 45, message = "IP address must have at most 45 characters")
    private String address;

    @Size(max = 255, message = "Description must have at most 255 characters")
    private String description;
}