package com.matrimony.backend.controller;

import java.security.Principal;
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
import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.UserRepo;
import com.matrimony.backend.Service.InterestService;

@RestController
public class InterestController {

    @Autowired
    private InterestService service;

    @Autowired
    private UserRepo repo;


    // =====================================================
    // SEND INTEREST
    // =====================================================

    @PostMapping("/send")
    public Optional<Interest> sendInterest(
            @RequestParam Long toUserId,
            Principal principal) throws Exception {

        // Get username from JWT/Principal
        String username = principal.getName();

        // Find authenticated user in database
        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Get actual logged-in user's ID
        Long fromUserId = user.getId();

        System.out.println(
                "Authenticated user ID: " + fromUserId
        );

        // Send interest
        return service.sendInterest(fromUserId, toUserId);
    }


    // =====================================================
    // INCOMING REQUESTS
    // =====================================================

    @GetMapping("/interests/incoming/{currentUserId}")
    public List<IncomingRequestDTO> getIncominRequest(
            @PathVariable Long currentUserId,
            Principal principal) {

        String username = principal.getName();

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();

        // Don't allow user to request another user's data
        if (!authenticatedUserId.equals(currentUserId)) {
            throw new RuntimeException(
                    "You cannot access another user's requests");
        }

        return service.getIncomingRequest(authenticatedUserId);
    }


    // =====================================================
    // SENT REQUESTS
    // =====================================================

    @GetMapping("/interests/sent/{currentUserId}")
    public List<Interest> getSentRequests(
            @PathVariable Long currentUserId,
            Principal principal) {

        String username = principal.getName();

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();

        if (!authenticatedUserId.equals(currentUserId)) {
            throw new RuntimeException(
                    "You cannot access another user's requests");
        }

        return service.getSentRequests(authenticatedUserId);
    }


    // =====================================================
    // ALL REQUESTS
    // =====================================================

    @GetMapping("/interests/AllRequest/{currentUserId}")
    public List<Interest> getAllRequest(
            @PathVariable Long currentUserId,
            Principal principal) {

        String username = principal.getName();

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();

        if (!authenticatedUserId.equals(currentUserId)) {
            throw new RuntimeException(
                    "You cannot access another user's requests");
        }

        return service.getAllRequests(authenticatedUserId);
    }


    // =====================================================
    // ACCEPT REQUEST
    // =====================================================

    @PutMapping("/accept")
    public Optional<Interest> acceptRequest(
            @RequestParam Long fromUserId,
            @RequestParam Long toUserId,
            Principal principal) {

        String username = principal.getName();

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();

        // The logged-in user must be the receiver
        if (!authenticatedUserId.equals(toUserId)) {
            throw new RuntimeException(
                    "You cannot accept a request for another user");
        }

        return service.AcceptRequest(
                fromUserId,
                authenticatedUserId
        );
    }


    // =====================================================
    // REJECT REQUEST
    // =====================================================

    @PutMapping("/reject")
    public Optional<Interest> rejectRequest(
            @RequestParam Long fromUserId,
            @RequestParam Long toUserId,
            Principal principal) {

        String username = principal.getName();

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();

        // The logged-in user must be the receiver
        if (!authenticatedUserId.equals(toUserId)) {
            throw new RuntimeException(
                    "You cannot reject a request for another user");
        }

        return service.RejectRequest(
                fromUserId,
                authenticatedUserId
        );
    }


    // =====================================================
    // GET MATCHES
    // =====================================================

    @GetMapping("/GetMatches/{currentUserId}")
    public List<MatchDTO> getMatches(
            @PathVariable Long currentUserId,
            Principal principal) {

        String username = principal.getName();

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();

        if (!authenticatedUserId.equals(currentUserId)) {
            throw new RuntimeException(
                    "You cannot access another user's matches");
        }

        return service.getMatches(authenticatedUserId);
    }


    // =====================================================
    // GET MATCHING PROFILES
    // =====================================================

    @GetMapping("/interests/profiles/{currentUserId}")
    public List<MatchProfileDTO> getMatchProfile(
            @PathVariable Long currentUserId,
            Principal principal) {

        String username = principal.getName();

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();

        if (!authenticatedUserId.equals(currentUserId)) {
            throw new RuntimeException(
                    "You cannot access another user's profiles");
        }

        return service.getAllProfilesForMatching(
                authenticatedUserId
        );
    }
}