package com.prf.prixstrategie.repository;

import com.prf.prixstrategie.entity.Competitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompetitorRepository extends JpaRepository<Competitor, UUID> {
    List<Competitor> findByProjectIdOrderBySortOrderAsc(UUID projectId);
}
