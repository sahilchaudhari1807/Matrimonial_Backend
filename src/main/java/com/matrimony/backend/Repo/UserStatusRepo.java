package com.matrimony.backend.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matrimony.backend.Model.UserStatus;

public interface UserStatusRepo extends JpaRepository<UserStatus,Long>{
   
	 UserStatus findByUserId(Long userId);
	
}
