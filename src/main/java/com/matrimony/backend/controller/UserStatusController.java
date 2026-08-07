package com.matrimony.backend.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matrimony.backend.Model.UserStatus;
import com.matrimony.backend.Repo.UserStatusRepo;
import com.matrimony.backend.Service.UserStatusService;

@RestController
@RequestMapping("/status")
public class UserStatusController {

  private final UserStatusService service;
  
  public UserStatusController(UserStatusService service) {
	  this.service=service;
  }
	
	@PutMapping("/online/{userId}/{online}")
	public void updateOnlineStatus(@PathVariable Long userId,@PathVariable boolean online) {
		 service.updateOnlineStatus(userId,online);
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<UserStatus> getUserStatus(@PathVariable Long userId) {

		System.out.println("GET STATUS HIT : " + userId);
		System.out.println("Controller HIT");
	    UserStatus userStatus = service.getUserStatus(userId).orElse(null);

	    if (userStatus == null) {
	        return ResponseEntity.notFound().build();
	    }

	   /* System.out.println("===== User Status =====");
	    System.out.println("Id: " + userStatus.getId());
	    System.out.println("UserId: " + userStatus.getUserId());
	    System.out.println("Online: " + userStatus.isOnline());
	    System.out.println("LastSeen: " + userStatus.getLastSeen());
	    System.out.println("=======================");*/

	    return ResponseEntity.ok(userStatus);
	}
	
	@PutMapping("/lastSeen/{userId}")
	public void updateLastSeen(@PathVariable Long userId) {
		service.updateOnlineStatus(userId,false);
	}
	
	
}
