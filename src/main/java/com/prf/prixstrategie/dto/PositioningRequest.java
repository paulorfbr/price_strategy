package com.prf.prixstrategie.dto;

import jakarta.validation.constraints.*;

public record PositioningRequest(
        @NotBlank @Size(max = 100) String axisXLeft,
        @NotBlank @Size(max = 100) String axisXRight,
        @NotBlank @Size(max = 100) String axisYTop,
        @NotBlank @Size(max = 100) String axisYBottom,
        @NotNull @Min(0) @Max(100) Short myX,
        @NotNull @Min(0) @Max(100) Short myY,
        @NotBlank @Size(max = 100) String myName
) {}
