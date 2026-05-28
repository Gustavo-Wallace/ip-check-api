package br.com.gustavo.ip_check_api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpAddressCsvImportRequestDTO {

    @NotBlank(message = "CSV content is required")
    private String csvContent;

    private Boolean analyzeAfterImport;
}