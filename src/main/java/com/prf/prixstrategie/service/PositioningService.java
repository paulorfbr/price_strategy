package com.prf.prixstrategie.service;

import com.prf.prixstrategie.dto.PositioningRequest;
import com.prf.prixstrategie.dto.PositioningResponse;
import com.prf.prixstrategie.entity.PositioningConfig;
import com.prf.prixstrategie.entity.PricingProject;
import com.prf.prixstrategie.repository.PositioningConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PositioningService {

    private final PositioningConfigRepository positioningRepo;
    private final ProjectService projectService;

    public PositioningService(PositioningConfigRepository positioningRepo, ProjectService projectService) {
        this.positioningRepo = positioningRepo;
        this.projectService = projectService;
    }

    @Transactional(readOnly = true)
    public PositioningResponse findByProject(UUID projectId) {
        PositioningConfig config = positioningRepo.findByProjectId(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Positioning not found for project: " + projectId));
        return toResponse(config);
    }

    public PositioningResponse upsert(UUID projectId, PositioningRequest req) {
        PricingProject project = projectService.requireProject(projectId);
        PositioningConfig config = positioningRepo.findByProjectId(projectId)
                .orElseGet(() -> {
                    PositioningConfig c = new PositioningConfig();
                    c.setProject(project);
                    return c;
                });
        config.setAxisXLeft(req.axisXLeft());
        config.setAxisXRight(req.axisXRight());
        config.setAxisYTop(req.axisYTop());
        config.setAxisYBottom(req.axisYBottom());
        config.setMyX(req.myX());
        config.setMyY(req.myY());
        config.setMyName(req.myName());
        return toResponse(positioningRepo.save(config));
    }

    static PositioningResponse toResponse(PositioningConfig c) {
        return new PositioningResponse(
                c.getAxisXLeft(), c.getAxisXRight(), c.getAxisYTop(), c.getAxisYBottom(),
                c.getMyX(), c.getMyY(), c.getMyName()
        );
    }
}
