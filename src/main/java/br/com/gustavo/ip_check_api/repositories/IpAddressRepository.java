package br.com.gustavo.ip_check_api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gustavo.ip_check_api.models.IpAddress;

public interface IpAddressRepository extends JpaRepository<IpAddress, Long> {

    Optional<IpAddress> findByAddress(String address);
}