package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.TagMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TagMappingRepo extends JpaRepository<TagMapping,Long> {
    Optional<TagMapping> findTagMappingByProjectIdAndTagId(Long projectId, Long tagId);
    List<TagMapping> findTagMappingByTagId(Long tagId);
//    List<TagMapping> findTagMappingsByTagId(List<Long> tagIds);
}
