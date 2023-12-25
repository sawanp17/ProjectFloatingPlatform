package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,String> {
    Optional<User> findUserByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE User SET isCoordinator=false")
    void setAllCoordinatorsFalse();

    @Modifying
    @Transactional
    @Query("UPDATE User SET isCoordinator = true WHERE username = ?1 ")
    void setAsCoordinator(String username);

}
