package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.Project;
import com.iitj.projectplatform.ProjectFilter;
import com.iitj.projectplatform.ProjectStatus;
import com.iitj.projectplatform.StipendOption;
import jakarta.persistence.Enumerated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {
    List<Project> findProjectByIsDeleted(Boolean isDeleted);
    Optional<Project> findProjectByTitle(String title);
    Project findProjectById(Long projectId);
    List<Project> findProjectByStatus(String status);

    List<Project> findByTitleIsContaining(String title);

    @Query("SELECT p FROM Project p WHERE p.deadline >= :deadline")
    List<Project> searchProjectByDeadline(@Param("deadline") Date deadline);

    List<Project> findProjectsByStipendOption(StipendOption stipendOption);
}

