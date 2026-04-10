package com.prf.prixstrategie.repository;

import com.prf.prixstrategie.entity.PriceSegment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PriceSegmentRepository extends JpaRepository<PriceSegment, UUID> {
    List<PriceSegment> findByProjectIdOrderBySortOrderAsc(UUID projectId);
    void deleteByProjectId(UUID projectId);
}
