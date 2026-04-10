package com.prf.prixstrategie.repository;

import com.prf.prixstrategie.entity.PositioningConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PositioningConfigRepository extends JpaRepository<PositioningConfig, UUID> {
    Optional<PositioningConfig> findByProjectId(UUID projectId);
}
