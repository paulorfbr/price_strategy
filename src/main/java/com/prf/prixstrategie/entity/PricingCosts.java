package com.prf.prixstrategie.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pricing_costs")
public class PricingCosts {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "project_id")
    private PricingProject project;

    @Column(name = "variable_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal variableCost;

    @Column(name = "fixed_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal fixedCost;

    @Column(name = "volume", nullable = false)
    private Integer volume;

    @Column(name = "target_margin", nullable = false, precision = 10, scale = 4)
    private BigDecimal targetMargin;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "alignment_price", precision = 19, scale = 4)
    private BigDecimal alignmentPrice;

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public PricingProject getProject() { return project; }
    public void setProject(PricingProject project) { this.project = project; }

    public BigDecimal getVariableCost() { return variableCost; }
    public void setVariableCost(BigDecimal variableCost) { this.variableCost = variableCost; }

    public BigDecimal getFixedCost() { return fixedCost; }
    public void setFixedCost(BigDecimal fixedCost) { this.fixedCost = fixedCost; }

    public Integer getVolume() { return volume; }
    public void setVolume(Integer volume) { this.volume = volume; }

    public BigDecimal getTargetMargin() { return targetMargin; }
    public void setTargetMargin(BigDecimal targetMargin) { this.targetMargin = targetMargin; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getAlignmentPrice() { return alignmentPrice; }
    public void setAlignmentPrice(BigDecimal alignmentPrice) { this.alignmentPrice = alignmentPrice; }
}
