package com.prf.prixstrategie.dto;

import com.prf.prixstrategie.entity.AnsoffQuadrant;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AnsoffResponse(
        UUID id,
        String name,
        AnsoffQuadrant quadrant,
        String description,
        Short sortOrder,
        OffsetDateTime createdAt
) {}
