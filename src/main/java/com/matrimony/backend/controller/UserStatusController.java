package com.matrimony.backend.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matrimony.backend.Model.UserStatus;
import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.UserRepo;
import com.matrimony.backend.Service.UserStatusService;

@RestController
@RequestMapping("/status")
public class UserStatusController {

    private final UserStatusService service;
    private final UserRepo userRepo;

    public UserStatusController(
            UserStatusService service,
            UserRepo userRepo) {

        this.service = service;
        this.userRepo = userRepo;
    }


    // =====================================================
    // UPDATE ONLINE STATUS
    // =====================================================

    @PutMapping("/online/{userId}/{online}")
    public void updateOnlineStatus(
            @PathVariable Long userId,
            @PathVariable boolean online,
            Principal principal) {

        // Get username from authenticated JWT
        String username = principal.getName();

        // Find logged-in user
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Get actual ID from authenticated user
        Long authenticatedUserId = user.getId();

        // Make sure user can update only their own status
        if (!authenticatedUserId.equals(userId)) {
            throw new RuntimeException(
                    "You cannot update another user's status"
            );
        }

        service.updateOnlineStatus(
                authenticatedUserId,
                online
        );
    }


    // =====================================================
    // GET USER STATUS
    // =====================================================

    @GetMapping("/{userId}")
    public ResponseEntity<UserStatus> getUserStatus(
            @PathVariable Long userId) {

        UserStatus userStatus =
                service.getUserStatus(userId).orElse(null);

        if (userStatus == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userStatus);
    }


    // =====================================================
    // UPDATE LAST SEEN
    // =====================================================

    @PutMapping("/lastSeen/{userId}")
    public void updateLastSeen(
            @PathVariable Long userId,
            Principal principal) {

        // Get username from authenticated JWT
        String username = principal.getName();

        // Find logged-in user
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Get actual authenticated user ID
        Long authenticatedUserId = user.getId();

        // Make sure user updates only their own status
        if (!authenticatedUserId.equals(userId)) {
            throw new RuntimeException(
                    "You cannot update another user's status"
            );
        }

        service.updateOnlineStatus(
                authenticatedUserId,
                false
        );
    }
}