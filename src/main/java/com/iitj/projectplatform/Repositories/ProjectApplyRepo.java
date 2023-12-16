package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.ProjectApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectApplyRepo extends JpaRepository<ProjectApply,Long> {
    List<Optional<ProjectApply>> findProjectApplyByUserId(String username);
    List<ProjectApply> findProjectApplyByProjectId(Long projectId);

//    List<Optional<ProjectApply>> findProjectApplyByProjectId(List<Long> projectId);
    Optional<ProjectApply> findProjectByUserIdAndProjectId(String username, Long projectId);
}
