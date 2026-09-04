package com.matrimony.backend.Config;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.matrimony.backend.Service.JWTservice;


// This interceptor works like a "filter" for WebSocket/STOMP messages.
// It allows us to check the JWT when the WebSocket connection is created.
public class JwtChannelInterceptor implements ChannelInterceptor {

    // We use our existing JWT service to extract and validate JWT.
    @Autowired
    private JWTservice jwtService;


    // preSend() runs BEFORE a STOMP message is processed.
    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {


        // Convert the normal Message into a STOMP-specific accessor.
        // This allows us to read STOMP commands and headers.
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(message);


        // Check whether this STOMP message is the CONNECT message.
        //
        // CONNECT happens when the frontend first establishes
        // the STOMP/WebSocket connection.
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {


            // Get the Authorization header sent by the frontend.
            //
            // Frontend sends:
            // Authorization: Bearer <JWT>
            String authHeader =
                    accessor.getFirstNativeHeader("Authorization");


            // Make sure the Authorization header exists
            // and starts with "Bearer ".
            if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {


                // Remove "Bearer " from the beginning.
                //
                // Example:
                // "Bearer abc123"
                //       ↓
                // "abc123"
                String token = authHeader.substring(7);


                try {


                    // Extract the username from the JWT.
                    //
                    // Example:
                    // JWT → chaitu@gmail.com
                    String username =
                            jwtService.extractUserName(token);


                    // Validate the JWT.
                    //
                    // Our JWT service checks:
                    // 1. Username matches
                    // 2. Token is not expired
                    jwtService.validateToken(
                            token,

                            // Create UserDetails containing
                            // the username extracted from JWT.
                            new org.springframework.security.core.userdetails.User(
                                    username,
                                    "",
                                    new java.util.ArrayList<>()
                            )
                    );


                    // Create a Principal representing
                    // the authenticated WebSocket user.
                    //
                    // Principal answers:
                    // "Who is connected?"
                    Principal principal =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    new java.util.ArrayList<>()
                            );


                    // Attach the Principal to this WebSocket session.
                    //
                    // From now on Spring knows:
                    // "This WebSocket connection belongs to this user."
                    accessor.setUser(principal);


                    // Debug message to verify authentication worked.
                    System.out.println(
                            "WebSocket authenticated: " + username
                    );


                } catch (Exception e) {


                    // If JWT is invalid, don't allow
                    // this STOMP CONNECT message to continue.
                    System.out.println(
                            "Invalid WebSocket JWT"
                    );

                    return null;
                }
            }
        }


        // Continue processing the STOMP message.
        return message;
    }
}