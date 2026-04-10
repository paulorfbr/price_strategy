package com.prf.prixstrategie.service;

import com.prf.prixstrategie.dto.StrategyRequest;
import com.prf.prixstrategie.dto.StrategyResponse;
import com.prf.prixstrategie.entity.PricingProject;
import com.prf.prixstrategie.entity.PricingStrategy;
import com.prf.prixstrategie.repository.PricingStrategyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class StrategyService {

    private final PricingStrategyRepository strategyRepo;
    private final ProjectService projectService;

    public StrategyService(PricingStrategyRepository strategyRepo, ProjectService projectService) {
        this.strategyRepo = strategyRepo;
        this.projectService = projectService;
    }

    @Transactional(readOnly = true)
    public StrategyResponse findByProject(UUID projectId) {
        PricingStrategy s = strategyRepo.findByProjectId(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Strategy not found for project: " + projectId));
        return toResponse(s);
    }

    public StrategyResponse upsert(UUID projectId, StrategyRequest req) {
        PricingProject project = projectService.requireProject(projectId);
        PricingStrategy strategy = strategyRepo.findByProjectId(projectId)
                .orElseGet(() -> {
                    PricingStrategy s = new PricingStrategy();
                    s.setProject(project);
                    return s;
                });
        strategy.setStrategy(req.strategy());
        strategy.setPricetype(req.priceType());
        return toResponse(strategyRepo.save(strategy));
    }

    static StrategyResponse toResponse(PricingStrategy s) {
        return new StrategyResponse(s.getStrategy(), s.getPricetype());
    }
}
