package com.prf.prixstrategie.controller;

import com.prf.prixstrategie.dto.CompetitorRequest;
import com.prf.prixstrategie.dto.CompetitorResponse;
import com.prf.prixstrategie.service.CompetitorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/competitors")
public class CompetitorController {

    private final CompetitorService competitorService;

    public CompetitorController(CompetitorService competitorService) {
        this.competitorService = competitorService;
    }

    @GetMapping
    public List<CompetitorResponse> list(@PathVariable UUID projectId) {
        return competitorService.findByProject(projectId);
    }

    @PostMapping
    public ResponseEntity<CompetitorResponse> create(@PathVariable UUID projectId,
                                                      @Valid @RequestBody CompetitorRequest req) {
        CompetitorResponse created = competitorService.create(projectId, req);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{competitorId}")
    public CompetitorResponse update(@PathVariable UUID projectId,
                                      @PathVariable UUID competitorId,
                                      @Valid @RequestBody CompetitorRequest req) {
        return competitorService.update(projectId, competitorId, req);
    }

    @DeleteMapping("/{competitorId}")
    public ResponseEntity<Void> delete(@PathVariable UUID projectId,
                                        @PathVariable UUID competitorId) {
        competitorService.delete(projectId, competitorId);
        return ResponseEntity.noContent().build();
    }
}
