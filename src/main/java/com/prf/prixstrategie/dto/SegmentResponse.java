package com.prf.prixstrategie.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SegmentResponse(
        UUID id,
        String name,
        BigDecimal multiplier,
        Short sortOrder
) {}
