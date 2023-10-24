package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.Attempts;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttemptsRepo extends JpaRepository<Attempts,Integer> {
    Optional<Attempts> findAttemptsByUsername(String username);
}
