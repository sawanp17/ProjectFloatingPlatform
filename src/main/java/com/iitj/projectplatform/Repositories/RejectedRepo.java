package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.Approved;
import com.iitj.projectplatform.Rejected;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RejectedRepo extends JpaRepository<Rejected,Long> {
    List<Rejected> findRejecteddByProjectId(Long projectId);
    Optional<Rejected> findRejectedByUserIdAndProjectId(String username, Long projectId);

}
