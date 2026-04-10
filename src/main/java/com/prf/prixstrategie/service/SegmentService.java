package com.prf.prixstrategie.service;

import com.prf.prixstrategie.dto.SegmentRequest;
import com.prf.prixstrategie.dto.SegmentResponse;
import com.prf.prixstrategie.entity.PriceSegment;
import com.prf.prixstrategie.entity.PricingProject;
import com.prf.prixstrategie.repository.PriceSegmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SegmentService {

    private final PriceSegmentRepository segmentRepo;
    private final ProjectService projectService;

    public SegmentService(PriceSegmentRepository segmentRepo, ProjectService projectService) {
        this.segmentRepo = segmentRepo;
        this.projectService = projectService;
    }

    @Transactional(readOnly = true)
    public List<SegmentResponse> findByProject(UUID projectId) {
        projectService.requireProject(projectId);
        return segmentRepo.findByProjectIdOrderBySortOrderAsc(projectId)
                .stream().map(SegmentService::toResponse).toList();
    }

    public SegmentResponse create(UUID projectId, SegmentRequest req) {
        PricingProject project = projectService.requireProject(projectId);
        PriceSegment seg = new PriceSegment();
        seg.setProject(project);
        seg.setName(req.name());
        seg.setMultiplier(req.multiplier());
        seg.setSortOrder(req.sortOrder() != null ? req.sortOrder() : (short) 0);
        return toResponse(segmentRepo.save(seg));
    }

    public SegmentResponse update(UUID projectId, UUID segmentId, SegmentRequest req) {
        PriceSegment seg = requireSegment(projectId, segmentId);
        seg.setName(req.name());
        seg.setMultiplier(req.multiplier());
        if (req.sortOrder() != null) seg.setSortOrder(req.sortOrder());
        return toResponse(segmentRepo.save(seg));
    }

    public void delete(UUID projectId, UUID segmentId) {
        PriceSegment seg = requireSegment(projectId, segmentId);
        segmentRepo.delete(seg);
    }

    private PriceSegment requireSegment(UUID projectId, UUID segmentId) {
        return segmentRepo.findById(segmentId)
                .filter(s -> s.getProject().getId().equals(projectId))
                .orElseThrow(() -> new EntityNotFoundException("Segment not found: " + segmentId));
    }

    static SegmentResponse toResponse(PriceSegment s) {
        return new SegmentResponse(s.getId(), s.getName(), s.getMultiplier(), s.getSortOrder());
    }
}
