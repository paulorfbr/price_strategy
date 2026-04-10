package com.prf.prixstrategie.dto;

import jakarta.validation.constraints.*;

public record CompetitorRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull @Min(0) @Max(100) Short positionX,
        @NotNull @Min(0) @Max(100) Short positionY,
        @NotBlank @Pattern(regexp = "#[0-9A-Fa-f]{6}") String color,
        Short sortOrder
) {}
