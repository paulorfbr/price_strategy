package com.prf.prixstrategie.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pricing_project")
public class PricingProject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PricingCosts costs;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PricingStrategy strategy;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PositioningConfig positioningConfig;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<PriceSegment> segments = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<Competitor> competitors = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<AnsoffInitiative> ansoffInitiatives = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public PricingCosts getCosts() { return costs; }
    public void setCosts(PricingCosts costs) { this.costs = costs; }

    public PricingStrategy getStrategy() { return strategy; }
    public void setStrategy(PricingStrategy strategy) { this.strategy = strategy; }

    public PositioningConfig getPositioningConfig() { return positioningConfig; }
    public void setPositioningConfig(PositioningConfig positioningConfig) { this.positioningConfig = positioningConfig; }

    public List<PriceSegment> getSegments() { return segments; }
    public void setSegments(List<PriceSegment> segments) { this.segments = segments; }

    public List<Competitor> getCompetitors() { return competitors; }
    public void setCompetitors(List<Competitor> competitors) { this.competitors = competitors; }

    public List<AnsoffInitiative> getAnsoffInitiatives() { return ansoffInitiatives; }
    public void setAnsoffInitiatives(List<AnsoffInitiative> ansoffInitiatives) { this.ansoffInitiatives = ansoffInitiatives; }
}
