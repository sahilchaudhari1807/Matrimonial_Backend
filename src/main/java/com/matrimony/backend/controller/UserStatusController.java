package com.matrimony.backend.controller;

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
	public UserStatus getUserStatus(@PathVariable Long userId) {
		return service.getUserStatus(userId);
	}
	
	@PutMapping("/lastSeen/{userId}")
	public void updateLastSeen(@PathVariable Long userId) {
		service.updateOnlineStatus(userId,false);
	}
	
	
}
