package com.prf.prixstrategie.repository;

import com.prf.prixstrategie.entity.AnsoffInitiative;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnsoffInitiativeRepository extends JpaRepository<AnsoffInitiative, UUID> {
    List<AnsoffInitiative> findByProjectIdOrderBySortOrderAsc(UUID projectId);
}
