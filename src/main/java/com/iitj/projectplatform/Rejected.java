package com.iitj.projectplatform;

import jakarta.persistence.*;

@Entity
@Table(name = "rejected")
public class Rejected {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "userid")
    private String userId;

    @Column(name = "projectid")
    private Long projectId;

    public Rejected(Long id, String userId, Long projectId) {
        this.id = id;
        this.userId = userId;
        this.projectId = projectId;
    }

    public Rejected() {
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
