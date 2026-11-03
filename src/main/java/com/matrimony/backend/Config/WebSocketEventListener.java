
package com.matrimony.backend.Config;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
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

    // Stores active WebSocket session IDs for each username
    private final ConcurrentHashMap<String, Set<String>> activeSessions =
            new ConcurrentHashMap<>();

    // Called after successful STOMP connection
    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = accessor.getUser();
        String sessionId = accessor.getSessionId();

        if (principal == null || sessionId == null) {
            return;
        }

        String username = principal.getName();

        activeSessions.compute(username, (key, sessions) -> {

            if (sessions == null) {
                sessions = ConcurrentHashMap.newKeySet();
            }

            sessions.add(sessionId);

            // User has an active connection
            Users user = userRepo.findByUsername(username).orElse(null);

            if (user != null) {
                statusService.updateOnlineStatus(user.getId(), true);
            }

            System.out.println("CONNECTED: " + username);
            System.out.println("Active sessions: " + sessions.size());

            return sessions;
        });
    }

    // Called when a STOMP session disconnects
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = accessor.getUser();
        String sessionId = accessor.getSessionId();

        if (principal == null || sessionId == null) {
            return;
        }

        String username = principal.getName();

        activeSessions.computeIfPresent(username, (key, sessions) -> {

            sessions.remove(sessionId);

            System.out.println("DISCONNECTED: " + username);
            System.out.println("Remaining sessions: " + sessions.size());

            // Only mark offline when the last connection disconnects
            if (sessions.isEmpty()) {

                Users user = userRepo.findByUsername(username).orElse(null);

                if (user != null) {
                    statusService.updateOnlineStatus(user.getId(), false);
                }

                System.out.println("USER IS NOW OFFLINE");

                return null;
            }

            return sessions;
        });
    }
}
