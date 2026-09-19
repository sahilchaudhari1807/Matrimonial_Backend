package com.matrimony.backend.Config;

import jakarta.servlet.FilterChain;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.UserRepo;
import com.matrimony.backend.Service.JWTservice;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	@Autowired
	private JWTservice jwtService;
	
	@Autowired
	private UserRepo repo;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
    	
    	String authHeader = request.getHeader("Authorization");
    	System.out.println("🔥 JWT FILTER: " 
    	        + request.getMethod() + " " 
    	        + request.getRequestURI());
    	if(authHeader!=null && authHeader.startsWith("Bearer ")) {
    		 String token=authHeader.substring(7);
    		 
    		 String username=jwtService.extractUserName(token);
    		 Users user = repo.findByUsername(username).orElse(null);
    		 
    		 UserDetails userDetails = new org.springframework.security.core.userdetails.User(
    			        user.getUsername(),
    			        user.getPassword(),
    			        new ArrayList<>()
    			);

    			boolean isValid = jwtService.validateToken(token, userDetails);

    			System.out.println("JWT valid: " + isValid);
    			
    			if (isValid) {
    				System.out.println("✅ JWT AUTHENTICATED: " + username);

    			    UsernamePasswordAuthenticationToken authentication =
    			            new UsernamePasswordAuthenticationToken(
    			                    userDetails,
    			                    null,
    			                    userDetails.getAuthorities()
    			            );

    			    SecurityContextHolder
    			            .getContext()
    			            .setAuthentication(authentication);
    			    
    			    System.out.println(
    			    	    "Authenticated user: " +
    			    	    SecurityContextHolder.getContext().getAuthentication().getName()
    			    	);
    			}
    	}
    	System.out.println(
    		    "🔥 REQUEST: " + request.getMethod() + " " + request.getRequestURI()
    		);

    		System.out.println(
    		    "🔥 AUTHENTICATION: " +
    		    SecurityContextHolder.getContext().getAuthentication()
    		);
        filterChain.doFilter(request, response);
        return;
    }
}