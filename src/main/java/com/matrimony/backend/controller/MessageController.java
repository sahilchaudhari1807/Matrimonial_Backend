package com.matrimony.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matrimony.backend.DTO.ChatPreviewDTO;
import com.matrimony.backend.Model.Message;
import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.UserRepo;
import com.matrimony.backend.Service.MessageService;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService msgService;
    private final UserRepo repo;

    // Constructor injection
    public MessageController(
            MessageService msgService,
            UserRepo repo) {

        this.msgService = msgService;
        this.repo = repo;
    }


    // =====================================================
    // SEND MESSAGE
    // =====================================================

    @PostMapping("/message")
    public Message sendMessage(
            @RequestBody Message message,
            Principal principal) {

        // Get authenticated username
        String username = principal.getName();

        // Find authenticated user
        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Get sender ID from authenticated user
        Long senderId = user.getId();

        // Don't trust senderId from frontend
        message.setSenderId(senderId);

        return msgService.saveMessage(message);
    }


    // =====================================================
    // GET MESSAGES
    // =====================================================

    @GetMapping("/{chatId}")
    public List<Message> getMessages(
            @PathVariable String chatId,Principal principal) {

    	    String username=principal.getName();
    	     
    	    Users user=repo.findByUsername(username).orElseThrow(()->new RuntimeException("user not found"));
    	    
    	    Long authenticatedUserId=user.getId();
    	    
    	
        return msgService.getMessagesByChatId(chatId,authenticatedUserId);
    }


    // =====================================================
    // MARK MESSAGE AS SEEN
    // =====================================================

    @PutMapping("/seen/{chatId}/{receiverId}")
    public void markMessage(
            @PathVariable String chatId,
            @PathVariable Long receiverId,
            Principal principal) {

        String username = principal.getName();

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();

        // Make sure receiverId belongs to logged-in user
        if (!authenticatedUserId.equals(receiverId)) {
            throw new RuntimeException(
                    "You cannot mark another user's messages as seen"
            );
        }

        msgService.markMessagesAsSeen(
                chatId,
                authenticatedUserId
        );
    }


    // =====================================================
    // GET UNREAD COUNT
    // =====================================================

    @GetMapping("/unread/{chatId}/{receiverId}")
    public long getUnreadCount(
            @PathVariable String chatId,
            @PathVariable Long receiverId,
            Principal principal) {

        String username = principal.getName();

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();

        // Make sure receiverId belongs to logged-in user
        if (!authenticatedUserId.equals(receiverId)) {
            throw new RuntimeException(
                    "You cannot access another user's unread count"
            );
        }

        return msgService.getUnreadCount(
                chatId,
                authenticatedUserId
        );
    }


    // =====================================================
    // CHAT DASHBOARD
    // =====================================================

    @GetMapping("/dashboard/{currentUserId}")
    public List<ChatPreviewDTO> getChatDashboard(
            @PathVariable Long currentUserId,
            Principal principal) {

        String username = principal.getName();
      

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();
      

        // User can only access their own dashboard
        if (!authenticatedUserId.equals(currentUserId)) {
            throw new RuntimeException(
                    "You cannot access another user's dashboard"
            );
        }

        return msgService.getChatDashboard(
                authenticatedUserId
        );
    }


    // =====================================================
    // MARK MESSAGE AS DELIVERED
    // =====================================================

    @PutMapping("/delivered/{messageId}")
    public void markMessageAsDelivered(
            @PathVariable Long messageId,
            Principal principal) {
    	  

        String username = principal.getName();

        Users user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long authenticatedUserId = user.getId();
       

        msgService.markMessageAsDelivered(
                messageId,
                authenticatedUserId
        );
    }
}