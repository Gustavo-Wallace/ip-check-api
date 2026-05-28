package br.com.gustavo.ip_check_api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.gustavo.ip_check_api.dtos.IpAddressCsvImportRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressImportRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressImportResponseDTO;
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
    @Operation(summary = "Register an IP address", description = "Registers a new IP address for future analysis.")
    public IpAddressResponseDTO create(@RequestBody @Valid IpAddressRequestDTO requestDTO) {
        return ipAddressService.create(requestDTO);
    }

    @GetMapping
    @Operation(summary = "List registered IP addresses", description = "Returns all registered IP addresses.")
    public List<IpAddressResponseDTO> findAll() {
        return ipAddressService.findAll();
    }

    @PostMapping("/import")
    @Operation(summary = "Import multiple IP addresses", description = "Imports multiple IP addresses from a JSON list, ignoring duplicates and reporting invalid entries.")
    public IpAddressImportResponseDTO importIpAddresses(
            @RequestBody @Valid IpAddressImportRequestDTO requestDTO) {
        return ipAddressService.importIpAddresses(requestDTO);
    }

    @GetMapping("/active")
    @Operation(summary = "List active IP addresses", description = "Returns only active registered IP addresses.")
    public List<IpAddressResponseDTO> findAllActive() {
        return ipAddressService.findAllActive();
    }

    @GetMapping("/page")
    @Operation(summary = "List registered IP addresses with pagination", description = "Returns registered IP addresses using pagination.")
    public Page<IpAddressResponseDTO> findAllPaged(Pageable pageable) {
        return ipAddressService.findAllPaged(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find IP address by ID", description = "Returns a registered IP address by its ID.")
    public IpAddressResponseDTO findById(@PathVariable Long id) {
        return ipAddressService.findById(id);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate IP address", description = "Deactivates a registered IP address without deleting it from the database.")
    public IpAddressResponseDTO deactivate(@PathVariable Long id) {
        return ipAddressService.deactivate(id);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate IP address", description = "Activates a previously deactivated IP address.")
    public IpAddressResponseDTO activate(@PathVariable Long id) {
        return ipAddressService.activate(id);
    }

    @PostMapping("/import/csv-text")
    @Operation(summary = "Import IP addresses from CSV text", description = "Imports IP addresses from a CSV text content. The first column must contain the IP address.")
    public IpAddressImportResponseDTO importIpAddressesFromCsvText(
            @RequestBody @Valid IpAddressCsvImportRequestDTO requestDTO) {
        return ipAddressService.importIpAddressesFromCsvText(requestDTO);
    }

}