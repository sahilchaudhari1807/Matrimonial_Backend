package com.matrimony.backend.Service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matrimony.backend.DTO.LoginResponseDTO;
import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.UserRepo;
import com.matrimony.backend.exception.ResourceAlreadyExistsException;
import com.matrimony.backend.exception.ResourceNotFoundException;
import com.matrimony.backend.exception.UnauthorizedException;



import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
    @Autowired
    private PasswordEncoder encoder;

    
    // ===========================
    // REGISTER NEW USER
    // ===========================
    public Users register(Users user) {

    	log.debug("registration started");
        // Check if username already exists
        boolean isExist = repo.existsByUsername(user.getUsername());
        

        if (isExist) {
        	log.warn("Registration failed: Username already exists");
            throw new ResourceAlreadyExistsException("User already exists");
        }

        // Encrypt password before saving
        user.setPassword(encoder.encode(user.getPassword()));
        
        Users users=repo.save(user);
        
        log.info("User registration successful");
        

        // Save user in database
        return users;
       
    }

    
    // ===========================
    // LOGIN / VERIFY USER
    // ===========================
    public LoginResponseDTO verify(Users user) {

        try {

            // Authenticate username and password
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword()
                )
            );

            // Check authentication result
            if (authentication.isAuthenticated()) {

                Users loggedInUser = repo.findByUsername(user.getUsername())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("User not found"));

                Long id = loggedInUser.getId();

                // Generate JWT
                String token = jwtservice.generateToken(user.getUsername());

                return new LoginResponseDTO(token, id);
            }

        } catch (org.springframework.security.core.AuthenticationException e) {

            log.warn("Login failed: Invalid credentials");

            throw new UnauthorizedException("Invalid username or password");
        }

        throw new UnauthorizedException("Login failed");
    }
}