package com.matrimony.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.UserRepo;



@Service
public class UserServices {
	
	@Autowired 
	private UserRepo repo;
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private JWTservice jwtservice;
	
	private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);
	
   public Users register(Users user) {
	   user.setPassword(encoder.encode(user.getPassword()));
	return repo.save(user);
	   
   }

   
   
   
   // authenticate user and then generate token
   public String verify(Users user) {

	    // 🔍 DEBUG LOGS (ADD HERE)
	    System.out.println("Username from request: " + user.getUsername());
	    System.out.println("Raw password from request: " + user.getPassword());

	    try {
	        Authentication authentication = authManager.authenticate(
	            new UsernamePasswordAuthenticationToken(
	                user.getUsername(),
	                user.getPassword()
	            )
	        );

	        if (authentication.isAuthenticated()) {
	            return jwtservice.generateToken(user.getUsername());
	        }

	    } catch (Exception e) {
	        e.printStackTrace(); // <-- IMPORTANT: see real cause
	        throw new RuntimeException("invalid username or password");
	    }

	    throw new RuntimeException("login failed");
	}

}
