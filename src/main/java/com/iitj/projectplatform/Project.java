package com.iitj.projectplatform;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "department")
    @Enumerated(EnumType.STRING)
    private Departments department;

    @Column(name = "deadline")
    private Date deadline;

    @Column(name = "description")
    private String description;

    @Column(name = "prereq")
    private String preReq;

    @Column(name = "maxlim")
    private int maxLim;

    @Column(name = "status")
    private String status;

    @Column(name = "projecttype")
    @Enumerated(EnumType.STRING)
    private ProjectType projectType;

    @Column(name = "stipendoption")
    @Enumerated(EnumType.STRING)
    private StipendOption stipendOption;

    @Column(name = "stipendamount")
    private Integer stipendAmount;

    @Column(name = "isdeleted")
    private Boolean isDeleted = false;

    public Project() {
    }


    public Project(String title, Departments department, Date deadline, String description, String preReq, int maxLim, String status) {
        this.title = title;
        this.department = department;
        this.deadline = deadline;
        this.description = description;
        this.preReq = preReq;
        this.maxLim = maxLim;
        this.status = status;
    }

    public StipendOption getStipendOption() {
        return stipendOption;
    }

    public void setStipendOption(StipendOption stipendOption) {
        this.stipendOption = stipendOption;
    }

    public Integer getStipendAmount() {
        return stipendAmount;
    }

    public void setStipendAmount(Integer stipendAmount) {
        this.stipendAmount = stipendAmount;
    }

    public Project(String title) {
        this.title = title;
    }


    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Departments getDepartment() {
        return department;
    }

    public void setDepartment(Departments department) {
        this.department = department;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreReq() {
        return preReq;
    }

    public void setPreReq(String preReq) {
        this.preReq = preReq;
    }

    public int getMaxLim() {
        return maxLim;
    }

    public void setMaxLim(int maxLim) {
        this.maxLim = maxLim;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
