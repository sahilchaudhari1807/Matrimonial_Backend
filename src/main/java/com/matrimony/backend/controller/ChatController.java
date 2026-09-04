package com.matrimony.backend.controller;

import java.security.Principal;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.matrimony.backend.DTO.TypingStatus;
import com.matrimony.backend.Model.Message;
import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.UserRepo;
import com.matrimony.backend.Service.MessageService;

@Controller
public class ChatController {

    private final MessageService msgService;
    private final UserRepo repo;

    // Constructor injection
    public ChatController(
            MessageService msgService,
            UserRepo repo) {

        this.msgService = msgService;
        this.repo = repo;
    }


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message receiveMessage(
            @Payload Message message,
            Principal principal) {

        // 1. Get username from authenticated Principal
        String username = principal.getName();

        System.out.println(
                "Authenticated user: " + username
        );


        // 2. Find the authenticated user in database
        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );


        // 3. Get actual ID of authenticated user
        Long authenticatedUserId = user.getId();

        System.out.println(
                "Authenticated User ID: "
                + authenticatedUserId
        );


        // 4. Set sender ID from authenticated user
        //    We DON'T trust senderId from frontend
        message.setSenderId(authenticatedUserId);

        System.out.println(
                "✅ Sender ID set by backend: "
                + message.getSenderId()
        );


        // 5. Save message
        Message saved = msgService.saveMessage(message);


        /*
        System.out.println("🔥 MESSAGE SAVED");

        System.out.println("Message ID: "
                + saved.getId());

        System.out.println("Sender: "
                + saved.getSenderId());

        System.out.println("Receiver: "
                + saved.getReceiverId());

        System.out.println(
                "🔥 RETURNING MESSAGE TO /topic/public"
        );
        */


        return saved;
    }


    @MessageMapping("/chat.typing")
    @SendTo("/topic/typingStatus")
    public TypingStatus typingStatus(
            @Payload TypingStatus status) {

        return status;
    }
}