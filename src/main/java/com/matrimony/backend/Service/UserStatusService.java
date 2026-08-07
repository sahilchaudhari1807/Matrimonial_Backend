package com.matrimony.backend.Service;

import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.matrimony.backend.Repo.UserStatusRepo;
import com.matrimony.backend.Model.UserStatus;


@Service
public class UserStatusService{
  
	private final UserStatusRepo userRepo;
	 private SimpMessagingTemplate messagingTemplate;
	
	public UserStatusService(UserStatusRepo userRepo, SimpMessagingTemplate messagingTemplate) {
		this.userRepo=userRepo;
		this.messagingTemplate=messagingTemplate;
	}
	
	public void updateOnlineStatus(Long userId, boolean online) {

	    System.out.println("\n========== UPDATE ONLINE STATUS ==========");

	    // 1. Method called
	    System.out.println("Service HIT");
	    System.out.println("UserId : " + userId);
	    System.out.println("Online : " + online);

	    // 2. Fetch from DB
	    UserStatus status = userRepo.findByUserId(userId).orElse(null);

	    System.out.println("\n===== BEFORE UPDATE =====");
	    System.out.println("UserId   : " + status.getUserId());
	    System.out.println("Online   : " + status.isOnline());
	    System.out.println("LastSeen : " + status.getLastSeen());

	    if (status == null) {

	        System.out.println("No existing record. Creating new UserStatus.");

	        status = new UserStatus();
	        status.setUserId(userId);
	    }

	    // 3. Update object
	    status.setOnline(online);
	    status.setLastSeen(System.currentTimeMillis());

	    // 4. Save
	    userRepo.save(status);

	    System.out.println("\n===== AFTER SAVE =====");
	    System.out.println("UserId   : " + status.getUserId());
	    System.out.println("Online   : " + status.isOnline());
	    System.out.println("LastSeen : " + status.getLastSeen());

	    // 5. Broadcast
	    System.out.println("\n===== SENDING TO WEBSOCKET =====");
	    System.out.println("UserId   : " + status.getUserId());
	    System.out.println("Online   : " + status.isOnline());
	    System.out.println("LastSeen : " + status.getLastSeen());

	    messagingTemplate.convertAndSend("/topic/status", status);

	    System.out.println("========== END ==========\n");
	}
	
	public Optional<UserStatus> getUserStatus(Long userId) {
		System.out.println("Service HIT");
	    return userRepo.findByUserId(userId);
	}
}
