package com.prf.prixstrategie.dto;

import java.util.UUID;

public record CompetitorResponse(
        UUID id,
        String name,
        Short positionX,
        Short positionY,
        String color,
        Short sortOrder
) {}
