package com.prf.prixstrategie.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ProjectSnapshotResponse(
        UUID id,
        String name,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        CostsResponse costs,
        StrategyResponse strategy,
        PositioningResponse positioning,
        List<SegmentResponse> segments,
        List<CompetitorResponse> competitors,
        List<AnsoffResponse> ansoffInitiatives
) {}
