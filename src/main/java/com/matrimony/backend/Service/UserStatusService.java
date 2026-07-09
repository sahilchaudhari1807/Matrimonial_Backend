package com.matrimony.backend.Service;

import org.springframework.stereotype.Service;

import com.matrimony.backend.Repo.UserStatusRepo;
import com.matrimony.backend.Model.UserStatus;


@Service
public class UserStatusService{
  
	private final UserStatusRepo userRepo;
	
	public UserStatusService(UserStatusRepo userRepo) {
		this.userRepo=userRepo;
	}
	
	public void updateOnlineStatus(Long userId,boolean online) {
		UserStatus status= userRepo.findByUserId(userId);
		
		System.out.println("CREATE USER HIT");
    	System.out.println("currentUserId = " + userId);
    	 System.out.println("online = " + online);
    	 
          
		 if (status == null) {

	            status = new UserStatus();

	            status.setUserId(userId);

	        }

	        // 🔹 Update online/offline
	        status.setOnline(online);

	        // 🔹 Update last seen
	        status.setLastSeen(
	                System.currentTimeMillis()
	        );
	        
	        userRepo.save(status);
	}
	
	public UserStatus getUserStatus(Long userId) {
		return userRepo.findByUserId(userId);
	}
}
