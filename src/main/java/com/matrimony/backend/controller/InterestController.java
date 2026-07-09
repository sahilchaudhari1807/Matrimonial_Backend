package com.matrimony.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matrimony.backend.DTO.IncomingRequestDTO;
import com.matrimony.backend.DTO.MatchDTO;
import com.matrimony.backend.DTO.MatchProfileDTO;
import com.matrimony.backend.Model.Interest;
import com.matrimony.backend.Service.InterestService;

@RestController
public class InterestController {

	 @Autowired
	 private InterestService service;
	
	@PostMapping("/send")
	public Optional<Interest> sendInterest(@RequestParam Long fromUserId,@RequestParam Long toUserId) throws Exception{
		return service.sendInterest(fromUserId, toUserId);
	}
	
	@GetMapping("/interests/incoming/{currentUserId}")
	public List<IncomingRequestDTO> getIncominRequest(@PathVariable Long currentUserId){
		return service.getIncomingRequest(currentUserId);
	}
	
	@GetMapping("/interests/sent/{currentUserId}")
	public List<Interest> getSentRequests(@PathVariable Long currentUserId){
		return service.getSentRequests(currentUserId);
		
	}
	
	@GetMapping("/interests/AllRequest/{currentUserId}")
	  public List<Interest> getAllRequest(@PathVariable Long currentUserId){
		return service.getAllRequests(currentUserId);
	}
	
	@PutMapping("/accept")
	public Optional<Interest> acceptRequest(
	        @RequestParam Long fromUserId,
	        @RequestParam Long toUserId) {

	    return service.AcceptRequest(fromUserId, toUserId);
	}
	
	@PutMapping("/reject")
	public Optional<Interest> rejectRequest(@RequestParam Long fromUserId,@RequestParam Long toUserId){
		return service.RejectRequest(fromUserId, toUserId);
	}
	
	@GetMapping("/GetMatches/{currentUserId}")
	public List<MatchDTO> getMatches(@PathVariable Long currentUserId){
	    return service.getMatches(currentUserId);
	}
	
	@GetMapping("/interests/profiles/{currentUserId}")
	public List<MatchProfileDTO> getMatchProfile(@PathVariable Long currentUserId){
		return service.getAllProfilesForMatching(currentUserId);
	}
	
}
