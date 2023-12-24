package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {
    Optional<Tag> findTagByName(String name);
//    List<Tag> findTagsByName(List<String> listOfTags);
}
