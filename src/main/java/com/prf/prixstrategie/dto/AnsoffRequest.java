package com.prf.prixstrategie.dto;

import com.prf.prixstrategie.entity.AnsoffQuadrant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnsoffRequest(
        @NotBlank @Size(max = 255) String name,
        @NotNull AnsoffQuadrant quadrant,
        String description,
        Short sortOrder
) {}
