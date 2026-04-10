package com.prf.prixstrategie.controller;

import com.prf.prixstrategie.dto.SegmentRequest;
import com.prf.prixstrategie.dto.SegmentResponse;
import com.prf.prixstrategie.service.SegmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/segments")
public class SegmentController {

    private final SegmentService segmentService;

    public SegmentController(SegmentService segmentService) {
        this.segmentService = segmentService;
    }

    @GetMapping
    public List<SegmentResponse> list(@PathVariable UUID projectId) {
        return segmentService.findByProject(projectId);
    }

    @PostMapping
    public ResponseEntity<SegmentResponse> create(@PathVariable UUID projectId,
                                                   @Valid @RequestBody SegmentRequest req) {
        SegmentResponse created = segmentService.create(projectId, req);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{segmentId}")
    public SegmentResponse update(@PathVariable UUID projectId,
                                   @PathVariable UUID segmentId,
                                   @Valid @RequestBody SegmentRequest req) {
        return segmentService.update(projectId, segmentId, req);
    }

    @DeleteMapping("/{segmentId}")
    public ResponseEntity<Void> delete(@PathVariable UUID projectId,
                                        @PathVariable UUID segmentId) {
        segmentService.delete(projectId, segmentId);
        return ResponseEntity.noContent().build();
    }
}
