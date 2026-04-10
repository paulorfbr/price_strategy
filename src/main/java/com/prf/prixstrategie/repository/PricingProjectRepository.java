package com.prf.prixstrategie.repository;

import com.prf.prixstrategie.entity.PricingProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PricingProjectRepository extends JpaRepository<PricingProject, UUID> {
}
