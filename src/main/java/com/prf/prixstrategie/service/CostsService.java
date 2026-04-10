package com.prf.prixstrategie.service;

import com.prf.prixstrategie.dto.CostsRequest;
import com.prf.prixstrategie.dto.CostsResponse;
import com.prf.prixstrategie.entity.PricingCosts;
import com.prf.prixstrategie.entity.PricingProject;
import com.prf.prixstrategie.repository.PricingCostsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CostsService {

    private final PricingCostsRepository costsRepo;
    private final ProjectService projectService;

    public CostsService(PricingCostsRepository costsRepo, ProjectService projectService) {
        this.costsRepo = costsRepo;
        this.projectService = projectService;
    }

    @Transactional(readOnly = true)
    public CostsResponse findByProject(UUID projectId) {
        PricingCosts costs = costsRepo.findByProjectId(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Costs not found for project: " + projectId));
        return toResponse(costs);
    }

    public CostsResponse upsert(UUID projectId, CostsRequest req) {
        PricingProject project = projectService.requireProject(projectId);
        PricingCosts costs = costsRepo.findByProjectId(projectId)
                .orElseGet(() -> {
                    PricingCosts c = new PricingCosts();
                    c.setProject(project);
                    return c;
                });
        costs.setVariableCost(req.variableCost());
        costs.setFixedCost(req.fixedCost());
        costs.setVolume(req.volume());
        costs.setTargetMargin(req.targetMargin());
        costs.setCurrency(req.currency());
        costs.setAlignmentPrice(req.alignmentPrice());
        return toResponse(costsRepo.save(costs));
    }

    static CostsResponse toResponse(PricingCosts c) {
        return new CostsResponse(
                c.getVariableCost(), c.getFixedCost(), c.getVolume(),
                c.getTargetMargin(), c.getCurrency(), c.getAlignmentPrice()
        );
    }
}
