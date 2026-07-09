package com.matrimony.backend.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.matrimony.backend.DTO.LoginResponseDTO;
import com.matrimony.backend.DTO.MatchDTO;
import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.UserRepo;

@Service
public class UserServices {

    // Inject User Repository
    @Autowired
    private UserRepo repo;

    // Used to authenticate username and password
    @Autowired
    private AuthenticationManager authManager;

    // Used to generate JWT token after successful login
    @Autowired
    private JWTservice jwtservice;

    // Encrypt password before storing in database
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    
    // ===========================
    // REGISTER NEW USER
    // ===========================
    public Users register(Users user) {

        // Check if username already exists
        boolean isExist = repo.existsByUsername(user.getUsername());

        if (isExist) {
            throw new RuntimeException("User already exists");
        }

        // Encrypt password before saving
        user.setPassword(encoder.encode(user.getPassword()));

        // Save user in database
        return repo.save(user);
    }

    
    // ===========================
    // LOGIN / VERIFY USER
    // ===========================
    public LoginResponseDTO verify(Users user) {

        // Debug logs
        System.out.println("Username from request: " + user.getUsername());
        System.out.println("Raw password from request: " + user.getPassword());

        try {

            // Authenticate username and password
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword()
                )
            );
            
       
            // If authentication successful
            if (authentication.isAuthenticated()) {
            	
            	/*Frontend sends username
                ↓
                UserRepo.findByUsername()
                ↓
                Fetches user from USERS table
                ↓
                Gets user ID
                ↓
                Generates token
                ↓
                Returns { token, id }*/

            	Users userId = repo.findByUsername(user.getUsername())
            	        .orElseThrow(() -> new RuntimeException("User not found"));
            	Long Id=userId.getId();
                // Generate JWT token
                 String token=jwtservice.generateToken(user.getUsername());
                 
                 LoginResponseDTO dto=new LoginResponseDTO(
                		token,Id
                		 
                		 );
                 
                 return dto;
            }

        } catch (Exception e) {

            // Print actual error in console
            e.printStackTrace();

            throw new RuntimeException("Invalid username or password");
        }

        // If authentication fails
        throw new RuntimeException("Login failed");
    }
}