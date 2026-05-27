package br.com.gustavo.ip_check_api.repositories;

import br.com.gustavo.ip_check_api.models.IpAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IpAnalysisRepository extends JpaRepository<IpAnalysis, Long> {

    List<IpAnalysis> findByAddress(String address);

    Optional<IpAnalysis> findTopByAddressOrderByAnalyzedAtDesc(String address);
}