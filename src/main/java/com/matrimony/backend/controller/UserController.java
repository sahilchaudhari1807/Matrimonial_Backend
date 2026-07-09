package com.matrimony.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matrimony.backend.DTO.LoginResponseDTO;
import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Service.UserServices;



@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UserController {
  
    
    private final UserServices Service;
    
    public UserController(UserServices Service) {
    	this.Service=Service;
    }
    
     
	  
	  
	
	  @PostMapping("/register")
	  public Users register(@RequestBody Users user) {
		return Service.register(user) ;
		  
	  }
	  
	/*  @PostMapping("/login")
	  public Map<String,String>verify(@RequestBody Users user) {
		  String token=Service.verify(user);
		  
		  Map<String,String> response=new HashMap<>();
		  response.put("token", token);
		  
		  return response;
	  }*/
	  @PostMapping("/login")
	  public LoginResponseDTO verify(@RequestBody Users user) {

	      return Service.verify(user);

	  }
}
