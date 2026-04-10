package com.prf.prixstrategie.dto;

import com.prf.prixstrategie.entity.PriceType;
import com.prf.prixstrategie.entity.StrategyType;

public record StrategyResponse(
        StrategyType strategy,
        PriceType priceType
) {}
