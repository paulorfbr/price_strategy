package com.prf.prixstrategie.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "pricing_strategy")
public class PricingStrategy {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "project_id")
    private PricingProject project;

    @Enumerated(EnumType.STRING)
    @Column(name = "strategy", nullable = false)
    private StrategyType strategy;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricetype", nullable = false)
    private PriceType pricetype;

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public PricingProject getProject() { return project; }
    public void setProject(PricingProject project) { this.project = project; }

    public StrategyType getStrategy() { return strategy; }
    public void setStrategy(StrategyType strategy) { this.strategy = strategy; }

    public PriceType getPricetype() { return pricetype; }
    public void setPricetype(PriceType pricetype) { this.pricetype = pricetype; }
}
