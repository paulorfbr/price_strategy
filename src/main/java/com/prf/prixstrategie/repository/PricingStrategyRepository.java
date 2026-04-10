package com.prf.prixstrategie.repository;

import com.prf.prixstrategie.entity.PricingStrategy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PricingStrategyRepository extends JpaRepository<PricingStrategy, UUID> {
    Optional<PricingStrategy> findByProjectId(UUID projectId);
}
