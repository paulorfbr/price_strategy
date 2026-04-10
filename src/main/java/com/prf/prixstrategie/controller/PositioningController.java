package com.prf.prixstrategie.controller;

import com.prf.prixstrategie.dto.PositioningRequest;
import com.prf.prixstrategie.dto.PositioningResponse;
import com.prf.prixstrategie.service.PositioningService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/positioning")
public class PositioningController {

    private final PositioningService positioningService;

    public PositioningController(PositioningService positioningService) {
        this.positioningService = positioningService;
    }

    @GetMapping
    public PositioningResponse get(@PathVariable UUID projectId) {
        return positioningService.findByProject(projectId);
    }

    @PutMapping
    public PositioningResponse upsert(@PathVariable UUID projectId,
                                       @Valid @RequestBody PositioningRequest req) {
        return positioningService.upsert(projectId, req);
    }
}
