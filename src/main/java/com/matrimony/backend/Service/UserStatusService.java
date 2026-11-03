package com.matrimony.backend.Service;

import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.matrimony.backend.Repo.UserStatusRepo;
import com.matrimony.backend.Model.UserStatus;


/*Your UserStatusService handles two main tasks:
1. Update a user's online/offline status in the database and broadcast the updated status through WebSocket.
2. Fetch a user's current status from the database.*/ 

@Service
public class UserStatusService{
  
	private final UserStatusRepo userRepo;
	 private final SimpMessagingTemplate messagingTemplate;
	
	public UserStatusService(UserStatusRepo userRepo, SimpMessagingTemplate messagingTemplate) {
		this.userRepo=userRepo;
		this.messagingTemplate=messagingTemplate;
	}
	

public void updateOnlineStatus(Long userId, boolean online) {

    UserStatus status = userRepo.findByUserId(userId).orElse(null);

    if (status == null) {
        status = new UserStatus();
        status.setUserId(userId);
    }

    status.setOnline(online);

    if (!online) {
        status.setLastSeen(System.currentTimeMillis());
    }

    userRepo.save(status);

    System.out.println("BROADCAST STATUS: userId="
            + userId + " online=" + online);

    messagingTemplate.convertAndSend("/topic/status", status);
}

	
	public Optional<UserStatus> getUserStatus(Long userId) {
		System.out.println("Service HIT");
	    return userRepo.findByUserId(userId);
	}
}
