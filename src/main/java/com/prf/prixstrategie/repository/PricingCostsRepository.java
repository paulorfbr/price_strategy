package com.prf.prixstrategie.repository;

import com.prf.prixstrategie.entity.PricingCosts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PricingCostsRepository extends JpaRepository<PricingCosts, UUID> {
    Optional<PricingCosts> findByProjectId(UUID projectId);
}
