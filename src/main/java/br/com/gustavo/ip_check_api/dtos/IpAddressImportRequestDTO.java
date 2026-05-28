package br.com.gustavo.ip_check_api.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IpAddressImportRequestDTO {

    @NotEmpty(message = "IP address list is required")
    private List<@Size(max = 45, message = "IP address must have at most 45 characters") String> addresses;

    @Size(max = 255, message = "Description must have at most 255 characters")
    private String description;
}