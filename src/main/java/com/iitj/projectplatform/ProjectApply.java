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

    @Column(name = "resumelink")
    private String resumeLink;

//    @Column(name="coursecode")
//    @Enumerated(EnumType.STRING)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "coursecode_id", referencedColumnName = "id")
    private CourseCode courseCode;

    @Column(name="isdeleted")
    private Boolean isDeleted = false;

    public ProjectApply() {
    }

    public ProjectApply(String user_id, Long project_id) {
        this.userId = user_id;
        this.projectId = project_id;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public CourseCode getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(CourseCode courseCode) {
        this.courseCode = courseCode;
    }

    public String getResumeLink() {
        return resumeLink;
    }

    public void setResumeLink(String resumeLink) {
        System.out.println("Here****************************************" + resumeLink);
        this.resumeLink = resumeLink;
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
