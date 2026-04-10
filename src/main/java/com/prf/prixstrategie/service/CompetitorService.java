package com.prf.prixstrategie.service;

import com.prf.prixstrategie.dto.CompetitorRequest;
import com.prf.prixstrategie.dto.CompetitorResponse;
import com.prf.prixstrategie.entity.Competitor;
import com.prf.prixstrategie.entity.PricingProject;
import com.prf.prixstrategie.repository.CompetitorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CompetitorService {

    private final CompetitorRepository competitorRepo;
    private final ProjectService projectService;

    public CompetitorService(CompetitorRepository competitorRepo, ProjectService projectService) {
        this.competitorRepo = competitorRepo;
        this.projectService = projectService;
    }

    @Transactional(readOnly = true)
    public List<CompetitorResponse> findByProject(UUID projectId) {
        projectService.requireProject(projectId);
        return competitorRepo.findByProjectIdOrderBySortOrderAsc(projectId)
                .stream().map(CompetitorService::toResponse).toList();
    }

    public CompetitorResponse create(UUID projectId, CompetitorRequest req) {
        PricingProject project = projectService.requireProject(projectId);
        Competitor c = new Competitor();
        c.setProject(project);
        c.setName(req.name());
        c.setPositionX(req.positionX());
        c.setPositionY(req.positionY());
        c.setColor(req.color());
        c.setSortOrder(req.sortOrder() != null ? req.sortOrder() : (short) 0);
        return toResponse(competitorRepo.save(c));
    }

    public CompetitorResponse update(UUID projectId, UUID competitorId, CompetitorRequest req) {
        Competitor c = requireCompetitor(projectId, competitorId);
        c.setName(req.name());
        c.setPositionX(req.positionX());
        c.setPositionY(req.positionY());
        c.setColor(req.color());
        if (req.sortOrder() != null) c.setSortOrder(req.sortOrder());
        return toResponse(competitorRepo.save(c));
    }

    public void delete(UUID projectId, UUID competitorId) {
        Competitor c = requireCompetitor(projectId, competitorId);
        competitorRepo.delete(c);
    }

    private Competitor requireCompetitor(UUID projectId, UUID competitorId) {
        return competitorRepo.findById(competitorId)
                .filter(c -> c.getProject().getId().equals(projectId))
                .orElseThrow(() -> new EntityNotFoundException("Competitor not found: " + competitorId));
    }

    static CompetitorResponse toResponse(Competitor c) {
        return new CompetitorResponse(c.getId(), c.getName(),
                c.getPositionX(), c.getPositionY(), c.getColor(), c.getSortOrder());
    }
}
