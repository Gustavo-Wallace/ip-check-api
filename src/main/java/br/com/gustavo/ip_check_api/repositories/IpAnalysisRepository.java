package br.com.gustavo.ip_check_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.gustavo.ip_check_api.enums.RiskLevel;
import br.com.gustavo.ip_check_api.models.IpAnalysis;

public interface IpAnalysisRepository extends JpaRepository<IpAnalysis, Long>, JpaSpecificationExecutor<IpAnalysis> {

    List<IpAnalysis> findByAddress(String address);

    List<IpAnalysis> findByRiskLevel(RiskLevel riskLevel);

    Optional<IpAnalysis> findTopByAddressOrderByAnalyzedAtDesc(String address);

    List<IpAnalysis> findByCountryIgnoreCase(String country);

    List<IpAnalysis> findByExternalProviderContainingIgnoreCase(String externalProvider);

    List<IpAnalysis> findByAsnIgnoreCase(String asn);
}