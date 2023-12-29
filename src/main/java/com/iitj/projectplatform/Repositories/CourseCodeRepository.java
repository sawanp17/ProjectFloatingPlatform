package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.CourseCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseCodeRepository extends JpaRepository<CourseCode,Long> {
    Optional<CourseCode> findCourseCodeByCode(String code);
}
