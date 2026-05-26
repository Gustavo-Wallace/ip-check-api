package br.com.gustavo.ip_check_api.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BatchAnalysisResponseDTO {

    private Integer totalProcessed;
    private Integer successCount;
    private Integer errorCount;
    private List<IpAnalysisResponseDTO> analyses;
    private List<BatchAnalysisErrorDTO> errors;
}