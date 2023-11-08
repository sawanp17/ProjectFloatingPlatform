package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.Approved;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovedRepo extends JpaRepository<Approved,Long> {
    List<Approved> findApprovedByProjectId(Long projectId);
}
