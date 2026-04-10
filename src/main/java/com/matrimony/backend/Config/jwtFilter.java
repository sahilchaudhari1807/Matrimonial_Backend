package com.matrimony.backend.Config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.matrimony.backend.Service.CustomUserDetailService;
import com.matrimony.backend.Service.JWTservice;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class jwtFilter extends OncePerRequestFilter {

	@Autowired
	private JWTservice jwtService;
	
	@Autowired
	private ApplicationContext context;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzYWhpbCIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwMDM2MDB9.XYZabc123signature
		
		// some steps are written untitled file to validate jwt
		String authHeader=request.getHeader("Authorization");
		String token=null;
		String username=null;
		
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			token=authHeader.substring(7);
			username=jwtService.extractUserName(token);
		}
			
		// this line check that user is not already authenticated
		if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
			UserDetails userDetail=context.getBean(CustomUserDetailService.class).loadUserByUsername(username);
			if(jwtService.validateToken(token,userDetail)) {
				
				/*WHO the user is
                IS the user authenticated -in this null bcoz it laready check pass so there is no need
               WHAT permissions (roles) the user has
               in below in arguments we check this */
				UsernamePasswordAuthenticationToken authtoken=new UsernamePasswordAuthenticationToken(userDetail,null,userDetail.getAuthorities());
				
				//it is option but good line	this setdetails
			 authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			 
			 /*
			  * JWT only proves identity,
                  SecurityContext tells Spring “this person is allowed inside.”*/
				 SecurityContextHolder.getContext().setAuthentication(authtoken);
			}
			
		}
		filterChain.doFilter(request,response);
	}

}
