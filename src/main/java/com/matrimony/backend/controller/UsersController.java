package com.matrimony.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.matrimony.backend.DTO.MatchDTO;
import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Model.UsersDetail;
import com.matrimony.backend.Repo.UserRepo;
import com.matrimony.backend.Service.UsersService;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UserRepo userRepo;


    // =====================================================
    // GET ALL USERS
    // =====================================================

    @GetMapping
    public List<MatchDTO> getAllUsers() {

        return usersService.getAllUsers();
    }


    // =====================================================
    // GET USER BY ID
    // =====================================================

    @GetMapping("/{id}")
    public UsersDetail getUserById(
            @PathVariable Long id) {

        return usersService.getUserById(id);
    }


    // =====================================================
    // CREATE USER PROFILE
    // =====================================================

    @PostMapping
    public UsersDetail createUser(
            @RequestBody UsersDetail user,
            Principal principal) {

        // Get username from JWT
        String username = principal.getName();

        // Find logged-in user
        Users loggedInUser =
                userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Get ID from authenticated user
        Long authenticatedUserId =
                loggedInUser.getId();

        // Create profile for logged-in user
        return usersService.createUser(
                user,
                authenticatedUserId
        );
    }


    // =====================================================
    // UPDATE USER PROFILE
    // =====================================================

    @PutMapping("/{id}")
    public UsersDetail updateUser(
            @PathVariable Long id,
            @RequestBody UsersDetail user,
            Principal principal) {

        // Get username from JWT
        String username = principal.getName();

        // Find logged-in user
        Users loggedInUser =
                userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Get actual authenticated ID
        Long authenticatedUserId =
                loggedInUser.getId();

        // Make sure the profile being updated
        // belongs to logged-in user
        if (!authenticatedUserId.equals(id)) {

            throw new RuntimeException(
                    "You cannot update another user's profile"
            );
        }

        return usersService.updateUser(
                id,
                user,
                authenticatedUserId
        );
    }
}