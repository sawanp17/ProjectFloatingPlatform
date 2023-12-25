package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.ProjectCreate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectCreateRepo extends JpaRepository<ProjectCreate,Long> {
    List<ProjectCreate> findProjectCreateByUserId(String username);
    ProjectCreate findProjectCreateByProjectId(Long projectId);

}
