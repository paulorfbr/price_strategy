package com.prf.prixstrategie.controller;

import com.prf.prixstrategie.dto.StrategyRequest;
import com.prf.prixstrategie.dto.StrategyResponse;
import com.prf.prixstrategie.service.StrategyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/strategy")
public class StrategyController {

    private final StrategyService strategyService;

    public StrategyController(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @GetMapping
    public StrategyResponse get(@PathVariable UUID projectId) {
        return strategyService.findByProject(projectId);
    }

    @PutMapping
    public StrategyResponse upsert(@PathVariable UUID projectId,
                                    @Valid @RequestBody StrategyRequest req) {
        return strategyService.upsert(projectId, req);
    }
}
