package br.com.gustavo.ip_check_api.models;

import java.time.LocalDateTime;

import br.com.gustavo.ip_check_api.enums.RiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ip_analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String address;

    @Column(nullable = false)
    private Boolean vpn;

    @Column(nullable = false)
    private Boolean proxy;

    @Column(nullable = false)
    private Boolean tor;

    @Column(nullable = false)
    private Boolean datacenter;

    @Column(nullable = false)
    private Boolean anonymous;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    @PrePersist
    public void prePersist() {
        if (vpn == null) {
            vpn = false;
        }

        if (proxy == null) {
            proxy = false;
        }

        if (tor == null) {
            tor = false;
        }

        if (datacenter == null) {
            datacenter = false;
        }

        if (anonymous == null) {
            anonymous = false;
        }

        if (analyzedAt == null) {
            analyzedAt = LocalDateTime.now();
        }
    }
}