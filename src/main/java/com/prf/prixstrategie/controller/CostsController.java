package com.prf.prixstrategie.controller;

import com.prf.prixstrategie.dto.CostsRequest;
import com.prf.prixstrategie.dto.CostsResponse;
import com.prf.prixstrategie.service.CostsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/costs")
public class CostsController {

    private final CostsService costsService;

    public CostsController(CostsService costsService) {
        this.costsService = costsService;
    }

    @GetMapping
    public CostsResponse get(@PathVariable UUID projectId) {
        return costsService.findByProject(projectId);
    }

    @PutMapping
    public CostsResponse upsert(@PathVariable UUID projectId,
                                 @Valid @RequestBody CostsRequest req) {
        return costsService.upsert(projectId, req);
    }
}
