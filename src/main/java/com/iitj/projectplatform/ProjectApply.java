package com.iitj.projectplatform;

import jakarta.persistence.*;

@Entity
@Table(name = "projectapply")
public class ProjectApply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "project_id")
    private Long projectId;

    public ProjectApply() {
    }

    public ProjectApply(String user_id, Long project_id) {
        this.userId = user_id;
        this.projectId = project_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
