package br.com.gustavo.ip_check_api.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class IpAddressImportResponseDTO {

    private Integer totalReceived;
    private Integer importedCount;
    private Integer duplicatedCount;
    private Integer errorCount;

    private List<IpAddressResponseDTO> imported;
    private List<String> duplicated;
    private List<IpAddressImportErrorDTO> errors;
    private List<IpAddressImportErrorDTO> importErrors;
    private List<IpAddressImportAnalysisErrorDTO> analysisErrors;
    private List<IpAnalysisResponseDTO> analyses;
}