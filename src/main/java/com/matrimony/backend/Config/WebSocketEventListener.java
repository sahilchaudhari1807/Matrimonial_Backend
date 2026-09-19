package com.matrimony.backend.Config;

import java.security.Principal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.UserRepo;
import com.matrimony.backend.Service.UserStatusService;

@Component
public class WebSocketEventListener {

	@Autowired
	private UserStatusService statusService;
	

    @Autowired
    private UserRepo userRepo;
    @EventListener
    public void handleWebSocketDisconnect(
            SessionDisconnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = accessor.getUser();

        System.out.println("🔴 DISCONNECT EVENT");

        if (principal != null) {

            String username = principal.getName();

            System.out.println(
                "Disconnected user: " + username
            );

            // update this user's status to offline
             Users user=userRepo.findByUsername(username).orElse(null);
             if(user!=null) {
            	 Long userId=user.getId();
                 System.out.println(
                         "🔥 User ID: " + userId
                 );

                 // Update database → OFFLINE
                 statusService.updateOnlineStatus(
                         userId,
                         false
                 );

                 System.out.println(
                         "✅ User marked OFFLINE"
                 );

             }
             else {

                 System.out.println(
                         "❌ User not found: " + username
                 );
             

        }
    }
}}