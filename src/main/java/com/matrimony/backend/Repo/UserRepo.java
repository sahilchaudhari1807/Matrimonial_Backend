package com.matrimony.backend.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.matrimony.backend.Model.Users;

public interface UserRepo extends JpaRepository<Users, Integer> {
    
    Optional<Users> findByUsername(String username); // ✅ UPDATED
}