package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.Project;
import com.iitj.projectplatform.ProjectStatus;
import jakarta.persistence.Enumerated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {
    Optional<Project> findProjectByTitle(String title);
    Project findProjectById(Long projectId);

    List<Project> findProjectByStatus(String status);
}

