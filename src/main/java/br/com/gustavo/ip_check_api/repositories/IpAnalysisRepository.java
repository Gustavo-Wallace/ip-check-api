package br.com.gustavo.ip_check_api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gustavo.ip_check_api.models.IpAnalysis;

public interface IpAnalysisRepository extends JpaRepository<IpAnalysis, Long> {

    List<IpAnalysis> findByAddress(String address);
}