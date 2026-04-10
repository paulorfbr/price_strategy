package com.prf.prixstrategie.service;

import com.prf.prixstrategie.dto.*;
import com.prf.prixstrategie.entity.*;
import com.prf.prixstrategie.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProjectService {

    private final PricingProjectRepository projectRepo;
    private final PriceSegmentRepository segmentRepo;
    private final CompetitorRepository competitorRepo;
    private final AnsoffInitiativeRepository ansoffRepo;

    public ProjectService(PricingProjectRepository projectRepo,
                          PriceSegmentRepository segmentRepo,
                          CompetitorRepository competitorRepo,
                          AnsoffInitiativeRepository ansoffRepo) {
        this.projectRepo = projectRepo;
        this.segmentRepo = segmentRepo;
        this.competitorRepo = competitorRepo;
        this.ansoffRepo = ansoffRepo;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepo.findAll().stream().map(this::toResponse).toList();
    }

    public ProjectResponse create(ProjectRequest req) {
        PricingProject project = new PricingProject();
        project.setName(req.name());
        project.setDescription(req.description());

        PricingCosts costs = new PricingCosts();
        costs.setProject(project);
        costs.setVariableCost(BigDecimal.ZERO);
        costs.setFixedCost(BigDecimal.ZERO);
        costs.setVolume(1);
        costs.setTargetMargin(new BigDecimal("30.00"));
        costs.setCurrency("EUR");
        project.setCosts(costs);

        PricingStrategy strategy = new PricingStrategy();
        strategy.setProject(project);
        strategy.setStrategy(StrategyType.luxury);
        strategy.setPricetype(PriceType.magic);
        project.setStrategy(strategy);

        PositioningConfig positioning = new PositioningConfig();
        positioning.setProject(project);
        positioning.setAxisXLeft("Bas Prix");
        positioning.setAxisXRight("Haut Prix");
        positioning.setAxisYTop("Haute Qualité");
        positioning.setAxisYBottom("Faible Qualité");
        positioning.setMyX((short) 60);
        positioning.setMyY((short) 75);
        positioning.setMyName("Mon Entreprise");
        project.setPositioningConfig(positioning);

        PricingProject saved = projectRepo.save(project);

        // Default segments
        createDefaultSegments(saved);

        return toResponse(saved);
    }

    private void createDefaultSegments(PricingProject project) {
        String[][] defaults = {{"Starter", "0.70"}, {"Pro", "1.00"}, {"Enterprise", "1.60"}};
        for (int i = 0; i < defaults.length; i++) {
            PriceSegment seg = new PriceSegment();
            seg.setProject(project);
            seg.setName(defaults[i][0]);
            seg.setMultiplier(new BigDecimal(defaults[i][1]));
            seg.setSortOrder((short) i);
            segmentRepo.save(seg);
        }
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(UUID id) {
        return toResponse(requireProject(id));
    }

    @Transactional(readOnly = true)
    public ProjectSnapshotResponse getSnapshot(UUID id) {
        PricingProject p = requireProject(id);
        List<PriceSegment> segments = segmentRepo.findByProjectIdOrderBySortOrderAsc(id);
        List<Competitor> competitors = competitorRepo.findByProjectIdOrderBySortOrderAsc(id);
        List<AnsoffInitiative> initiatives = ansoffRepo.findByProjectIdOrderBySortOrderAsc(id);

        return new ProjectSnapshotResponse(
                p.getId(), p.getName(), p.getDescription(), p.getCreatedAt(), p.getUpdatedAt(),
                p.getCosts() != null ? CostsService.toResponse(p.getCosts()) : null,
                p.getStrategy() != null ? StrategyService.toResponse(p.getStrategy()) : null,
                p.getPositioningConfig() != null ? PositioningService.toResponse(p.getPositioningConfig()) : null,
                segments.stream().map(SegmentService::toResponse).toList(),
                competitors.stream().map(CompetitorService::toResponse).toList(),
                initiatives.stream().map(AnsoffService::toResponse).toList()
        );
    }

    public ProjectResponse update(UUID id, ProjectRequest req) {
        PricingProject project = requireProject(id);
        project.setName(req.name());
        project.setDescription(req.description());
        return toResponse(projectRepo.save(project));
    }

    public void delete(UUID id) {
        requireProject(id);
        projectRepo.deleteById(id);
    }

    PricingProject requireProject(UUID id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + id));
    }

    private ProjectResponse toResponse(PricingProject p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getDescription(),
                p.getCreatedAt(), p.getUpdatedAt());
    }
}
