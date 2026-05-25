package br.com.gustavo.ip_check_api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavo.ip_check_api.dtos.IpAddressRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressResponseDTO;
import br.com.gustavo.ip_check_api.services.IpAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ips")
@RequiredArgsConstructor
@Tag(name = "IP Addresses", description = "Endpoints for registering and listing IP addresses")
public class IpAddressController {

    private final IpAddressService ipAddressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register an IP address",
            description = "Registers a new IP address for future analysis."
    )
    public IpAddressResponseDTO create(@RequestBody @Valid IpAddressRequestDTO requestDTO) {
        return ipAddressService.create(requestDTO);
    }

    @GetMapping
    @Operation(
            summary = "List registered IP addresses",
            description = "Returns all registered IP addresses."
    )
    public List<IpAddressResponseDTO> findAll() {
        return ipAddressService.findAll();
    }
}