package com.prf.prixstrategie.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "positioning_config")
public class PositioningConfig {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "project_id")
    private PricingProject project;

    @Column(name = "axis_x_left", nullable = false)
    private String axisXLeft;

    @Column(name = "axis_x_right", nullable = false)
    private String axisXRight;

    @Column(name = "axis_y_top", nullable = false)
    private String axisYTop;

    @Column(name = "axis_y_bottom", nullable = false)
    private String axisYBottom;

    @Column(name = "my_x", nullable = false)
    private Short myX;

    @Column(name = "my_y", nullable = false)
    private Short myY;

    @Column(name = "my_name", nullable = false)
    private String myName;

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public PricingProject getProject() { return project; }
    public void setProject(PricingProject project) { this.project = project; }

    public String getAxisXLeft() { return axisXLeft; }
    public void setAxisXLeft(String axisXLeft) { this.axisXLeft = axisXLeft; }

    public String getAxisXRight() { return axisXRight; }
    public void setAxisXRight(String axisXRight) { this.axisXRight = axisXRight; }

    public String getAxisYTop() { return axisYTop; }
    public void setAxisYTop(String axisYTop) { this.axisYTop = axisYTop; }

    public String getAxisYBottom() { return axisYBottom; }
    public void setAxisYBottom(String axisYBottom) { this.axisYBottom = axisYBottom; }

    public Short getMyX() { return myX; }
    public void setMyX(Short myX) { this.myX = myX; }

    public Short getMyY() { return myY; }
    public void setMyY(Short myY) { this.myY = myY; }

    public String getMyName() { return myName; }
    public void setMyName(String myName) { this.myName = myName; }
}
