package com.prf.prixstrategie.dto;

import java.math.BigDecimal;

public record CostsResponse(
        BigDecimal variableCost,
        BigDecimal fixedCost,
        Integer volume,
        BigDecimal targetMargin,
        String currency,
        BigDecimal alignmentPrice
) {}
