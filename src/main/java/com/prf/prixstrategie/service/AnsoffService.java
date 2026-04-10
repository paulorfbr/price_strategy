package com.prf.prixstrategie.service;

import com.prf.prixstrategie.dto.AnsoffRequest;
import com.prf.prixstrategie.dto.AnsoffResponse;
import com.prf.prixstrategie.entity.AnsoffInitiative;
import com.prf.prixstrategie.entity.PricingProject;
import com.prf.prixstrategie.repository.AnsoffInitiativeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AnsoffService {

    private final AnsoffInitiativeRepository ansoffRepo;
    private final ProjectService projectService;

    public AnsoffService(AnsoffInitiativeRepository ansoffRepo, ProjectService projectService) {
        this.ansoffRepo = ansoffRepo;
        this.projectService = projectService;
    }

    @Transactional(readOnly = true)
    public List<AnsoffResponse> findByProject(UUID projectId) {
        projectService.requireProject(projectId);
        return ansoffRepo.findByProjectIdOrderBySortOrderAsc(projectId)
                .stream().map(AnsoffService::toResponse).toList();
    }

    public AnsoffResponse create(UUID projectId, AnsoffRequest req) {
        PricingProject project = projectService.requireProject(projectId);
        AnsoffInitiative initiative = new AnsoffInitiative();
        initiative.setProject(project);
        initiative.setName(req.name());
        initiative.setQuadrant(req.quadrant());
        initiative.setDescription(req.description());
        initiative.setSortOrder(req.sortOrder() != null ? req.sortOrder() : (short) 0);
        return toResponse(ansoffRepo.save(initiative));
    }

    public AnsoffResponse update(UUID projectId, UUID initiativeId, AnsoffRequest req) {
        AnsoffInitiative initiative = requireInitiative(projectId, initiativeId);
        initiative.setName(req.name());
        initiative.setQuadrant(req.quadrant());
        initiative.setDescription(req.description());
        if (req.sortOrder() != null) initiative.setSortOrder(req.sortOrder());
        return toResponse(ansoffRepo.save(initiative));
    }

    public void delete(UUID projectId, UUID initiativeId) {
        AnsoffInitiative initiative = requireInitiative(projectId, initiativeId);
        ansoffRepo.delete(initiative);
    }

    private AnsoffInitiative requireInitiative(UUID projectId, UUID initiativeId) {
        return ansoffRepo.findById(initiativeId)
                .filter(i -> i.getProject().getId().equals(projectId))
                .orElseThrow(() -> new EntityNotFoundException("Initiative not found: " + initiativeId));
    }

    static AnsoffResponse toResponse(AnsoffInitiative i) {
        return new AnsoffResponse(i.getId(), i.getName(), i.getQuadrant(),
                i.getDescription(), i.getSortOrder(), i.getCreatedAt());
    }
}
