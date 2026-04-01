package com.prf.prixstrategie.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "competitor")
public class Competitor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private PricingProject project;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "position_x", nullable = false)
    private Short positionX;

    @Column(name = "position_y", nullable = false)
    private Short positionY;

    @Column(name = "color", nullable = false, length = 7)
    private String color;

    @Column(name = "sort_order", nullable = false)
    private Short sortOrder;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PricingProject getProject() { return project; }
    public void setProject(PricingProject project) { this.project = project; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Short getPositionX() { return positionX; }
    public void setPositionX(Short positionX) { this.positionX = positionX; }

    public Short getPositionY() { return positionY; }
    public void setPositionY(Short positionY) { this.positionY = positionY; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Short getSortOrder() { return sortOrder; }
    public void setSortOrder(Short sortOrder) { this.sortOrder = sortOrder; }
}
