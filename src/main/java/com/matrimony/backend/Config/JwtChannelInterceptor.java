package com.matrimony.backend.Config;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.matrimony.backend.Service.JWTservice;

public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JWTservice jwtService;

    // Constructor injection
    public JwtChannelInterceptor(JWTservice jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {

        // Get STOMP accessor from the message
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor == null) {

            System.out.println(
                    "❌ STOMP accessor is NULL"
            );

            return message;
        }

        System.out.println(
                "STOMP Command: "
                + accessor.getCommand()
        );

        // =====================================================
        // CONNECT
        // =====================================================

        if (StompCommand.CONNECT.equals(
                accessor.getCommand())) {

            // Get JWT from STOMP CONNECT headers
            String authHeader =
                    accessor.getFirstNativeHeader(
                            "Authorization"
                    );

            System.out.println(
                    "Authorization header present: "
                    + (authHeader != null)
            );

            // Check Authorization header
            if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

                System.out.println(
                        "❌ WebSocket Authorization header missing"
                );

                return null;
            }

            // Remove "Bearer "
            String token =
                    authHeader.substring(7);

            try {

                // =================================================
                // EXTRACT USERNAME
                // =================================================

                String username =
                        jwtService.extractUserName(token);

                System.out.println(
                        "JWT Username: "
                        + username
                );

                // =================================================
                // CREATE USER DETAILS
                // =================================================

                UserDetails userDetails =
                        new User(
                                username,
                                "",
                                new ArrayList<>()
                        );

                // =================================================
                // VALIDATE JWT
                // =================================================

                boolean valid =
                        jwtService.validateToken(
                                token,
                                userDetails
                        );

                if (!valid) {

                    System.out.println(
                            "❌ Invalid or expired WebSocket JWT"
                    );

                    return null;
                }

                // =================================================
                // CREATE PRINCIPAL
                // =================================================

                Principal principal =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                new ArrayList<>()
                        );

                // =================================================
                // ATTACH PRINCIPAL
                // =================================================

                accessor.setUser(principal);

                System.out.println(
                        "✅ WebSocket authenticated: "
                        + username
                );

                System.out.println(
                        "✅ Principal set: "
                        + accessor.getUser()
                );

                // =================================================
                // KEEP ACCESSOR MUTABLE
                // =================================================

                accessor.setLeaveMutable(true);

                // =================================================
                // RETURN MESSAGE WITH MODIFIED HEADERS
                // =================================================

                return MessageBuilder.createMessage(
                        message.getPayload(),
                        accessor.getMessageHeaders()
                );

            } catch (Exception e) {

                System.out.println(
                        "❌ Invalid WebSocket JWT: "
                        + e.getMessage()
                );

                e.printStackTrace();

                return null;
            }
        }

        // =====================================================
        // SUBSCRIBE / SEND / DISCONNECT
        // =====================================================

        System.out.println(
                "Principal for "
                + accessor.getCommand()
                + ": "
                + accessor.getUser()
        );

        return message;
    }
}