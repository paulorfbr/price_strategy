package com.prf.prixstrategie.controller;

import com.prf.prixstrategie.dto.AnsoffRequest;
import com.prf.prixstrategie.dto.AnsoffResponse;
import com.prf.prixstrategie.service.AnsoffService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/ansoff")
public class AnsoffController {

    private final AnsoffService ansoffService;

    public AnsoffController(AnsoffService ansoffService) {
        this.ansoffService = ansoffService;
    }

    @GetMapping
    public List<AnsoffResponse> list(@PathVariable UUID projectId) {
        return ansoffService.findByProject(projectId);
    }

    @PostMapping
    public ResponseEntity<AnsoffResponse> create(@PathVariable UUID projectId,
                                                  @Valid @RequestBody AnsoffRequest req) {
        AnsoffResponse created = ansoffService.create(projectId, req);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{initiativeId}")
    public AnsoffResponse update(@PathVariable UUID projectId,
                                  @PathVariable UUID initiativeId,
                                  @Valid @RequestBody AnsoffRequest req) {
        return ansoffService.update(projectId, initiativeId, req);
    }

    @DeleteMapping("/{initiativeId}")
    public ResponseEntity<Void> delete(@PathVariable UUID projectId,
                                        @PathVariable UUID initiativeId) {
        ansoffService.delete(projectId, initiativeId);
        return ResponseEntity.noContent().build();
    }
}
