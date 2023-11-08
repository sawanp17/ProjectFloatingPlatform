package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.Approved;
import com.iitj.projectplatform.Rejected;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RejectedRepo extends JpaRepository<Rejected,Long> {
    List<Rejected> findRejecteddByProjectId(Long projectId);

}
