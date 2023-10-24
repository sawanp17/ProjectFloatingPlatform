package com.iitj.projectplatform.Repositories;

import com.iitj.projectplatform.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,String> {
    Optional<User> findUserByUsername(String username);
}
