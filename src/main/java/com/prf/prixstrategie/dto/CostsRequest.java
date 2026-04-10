package com.prf.prixstrategie.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CostsRequest(
        @NotNull @DecimalMin("0") BigDecimal variableCost,
        @NotNull @DecimalMin("0") BigDecimal fixedCost,
        @NotNull @Min(1) Integer volume,
        @NotNull @DecimalMin("0") @DecimalMax(value = "100", inclusive = false) BigDecimal targetMargin,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @DecimalMin("0") BigDecimal alignmentPrice
) {}
