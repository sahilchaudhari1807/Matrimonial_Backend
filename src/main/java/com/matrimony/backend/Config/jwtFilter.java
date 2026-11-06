
package com.matrimony.backend.Config;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.UserRepo;
import com.matrimony.backend.Service.JWTservice;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTservice jwtService;
    private final UserRepo repo;

    public JwtFilter(JWTservice jwtService, UserRepo repo) {
        this.jwtService = jwtService;
        this.repo = repo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        SecurityContextHolder.clearContext();

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                String username = jwtService.extractUserName(token);

                Users user = repo.findByUsername(username).orElse(null);

                if (user != null) {

                    UserDetails userDetails =
                            new org.springframework.security.core.userdetails.User(
                                    user.getUsername(),
                                    user.getPassword(),
                                    new ArrayList<>()
                            );

                    boolean isValid =
                            jwtService.validateToken(token, userDetails);

                    if (isValid) {

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                }

            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
