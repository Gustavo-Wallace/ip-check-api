package br.com.gustavo.ip_check_api.services;

import java.net.InetAddress;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.gustavo.ip_check_api.dtos.IpAddressRequestDTO;
import br.com.gustavo.ip_check_api.dtos.IpAddressResponseDTO;
import br.com.gustavo.ip_check_api.models.IpAddress;
import br.com.gustavo.ip_check_api.repositories.IpAddressRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpAddressService {

    private final IpAddressRepository ipAddressRepository;

    public IpAddressResponseDTO create(IpAddressRequestDTO requestDTO) {
        validateIpAddress(requestDTO.getAddress());

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

    private void validateIpAddress(String address) {
        try {
            InetAddress.getByName(address);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid IP address");
        }
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
}