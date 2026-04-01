package com.prf.prixstrategie.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "price_segment")
public class PriceSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private PricingProject project;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "multiplier", nullable = false, precision = 10, scale = 4)
    private BigDecimal multiplier;

    @Column(name = "sort_order", nullable = false)
    private Short sortOrder;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PricingProject getProject() { return project; }
    public void setProject(PricingProject project) { this.project = project; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getMultiplier() { return multiplier; }
    public void setMultiplier(BigDecimal multiplier) { this.multiplier = multiplier; }

    public Short getSortOrder() { return sortOrder; }
    public void setSortOrder(Short sortOrder) { this.sortOrder = sortOrder; }
}
