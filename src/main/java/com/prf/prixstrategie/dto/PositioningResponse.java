package com.prf.prixstrategie.dto;

public record PositioningResponse(
        String axisXLeft,
        String axisXRight,
        String axisYTop,
        String axisYBottom,
        Short myX,
        Short myY,
        String myName
) {}
