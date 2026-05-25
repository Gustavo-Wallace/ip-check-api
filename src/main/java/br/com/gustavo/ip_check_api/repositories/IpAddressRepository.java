package br.com.gustavo.ip_check_api.repositories;

import br.com.gustavo.ip_check_api.models.IpAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IpAddressRepository extends JpaRepository<IpAddress, Long> {

    Optional<IpAddress> findByAddress(String address);

    List<IpAddress> findByActiveTrue();
}