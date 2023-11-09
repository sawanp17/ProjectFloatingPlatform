package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.Approved;
import com.iitj.projectplatform.ProjectApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovedRepo extends JpaRepository<Approved,Long> {
    List<Approved> findApprovedByProjectId(Long projectId);
    Optional<Approved> findApprovedByUserIdAndProjectId(String username, Long projectId);

}
