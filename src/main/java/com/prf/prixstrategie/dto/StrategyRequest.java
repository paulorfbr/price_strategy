package com.prf.prixstrategie.dto;

import com.prf.prixstrategie.entity.PriceType;
import com.prf.prixstrategie.entity.StrategyType;
import jakarta.validation.constraints.NotNull;

public record StrategyRequest(
        @NotNull StrategyType strategy,
        @NotNull PriceType priceType
) {}
