package com.prf.prixstrategie.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ansoff_initiative")
public class AnsoffInitiative {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private PricingProject project;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "quadrant", nullable = false)
    private AnsoffQuadrant quadrant;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "sort_order", nullable = false)
    private Short sortOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PricingProject getProject() { return project; }
    public void setProject(PricingProject project) { this.project = project; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AnsoffQuadrant getQuadrant() { return quadrant; }
    public void setQuadrant(AnsoffQuadrant quadrant) { this.quadrant = quadrant; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Short getSortOrder() { return sortOrder; }
    public void setSortOrder(Short sortOrder) { this.sortOrder = sortOrder; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
