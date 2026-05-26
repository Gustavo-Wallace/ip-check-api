package br.com.gustavo.ip_check_api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.gustavo.ip_check_api.dtos.IpAddressRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressResponseDTO;
import br.com.gustavo.ip_check_api.exceptions.ResourceNotFoundException;
import br.com.gustavo.ip_check_api.models.IpAddress;
import br.com.gustavo.ip_check_api.repositories.IpAddressRepository;
import br.com.gustavo.ip_check_api.utils.IpValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpAddressService {

    private final IpAddressRepository ipAddressRepository;

    public IpAddressResponseDTO create(IpAddressRequestDTO requestDTO) {
        IpValidator.validate(requestDTO.getAddress());

        if (ipAddressRepository.findByAddress(requestDTO.getAddress()).isPresent()) {
            throw new IllegalArgumentException("IP address already registered");
        }

        IpAddress ipAddress = IpAddress.builder()
                .address(requestDTO.getAddress())
                .description(requestDTO.getDescription())
                .build();

        IpAddress savedIpAddress = ipAddressRepository.save(ipAddress);

        return toResponseDTO(savedIpAddress);
    }

    public List<IpAddressResponseDTO> findAll() {
        return ipAddressRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private IpAddressResponseDTO toResponseDTO(IpAddress ipAddress) {
        return IpAddressResponseDTO.builder()
                .id(ipAddress.getId())
                .address(ipAddress.getAddress())
                .description(ipAddress.getDescription())
                .active(ipAddress.getActive())
                .createdAt(ipAddress.getCreatedAt())
                .build();
    }

    public IpAddressResponseDTO findById(Long id) {
        IpAddress ipAddress = ipAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IP address not found"));

        return toResponseDTO(ipAddress);
    }

    public IpAddressResponseDTO deactivate(Long id) {
        IpAddress ipAddress = ipAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IP address not found"));

        ipAddress.setActive(false);

        IpAddress updatedIpAddress = ipAddressRepository.save(ipAddress);

        return toResponseDTO(updatedIpAddress);
    }

    public IpAddressResponseDTO activate(Long id) {
        IpAddress ipAddress = ipAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IP address not found"));

        ipAddress.setActive(true);

        IpAddress updatedIpAddress = ipAddressRepository.save(ipAddress);

        return toResponseDTO(updatedIpAddress);
    }

    public List<IpAddressResponseDTO> findAllActive() {
        return ipAddressRepository.findByActiveTrue()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

}