package com.matrimony.backend.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.matrimony.backend.DTO.ChatPreviewDTO;
import com.matrimony.backend.Model.Message;
import com.matrimony.backend.Service.MessageService;

@RestController
@RequestMapping("/messages")
public class MessageController {

	 private final MessageService msgService;

	   // constructor injection we can autowired but sometimes autowired gets error
	    public MessageController(MessageService msgService) {
	        this.msgService = msgService;
	    }

    @PostMapping("/message")
    public Message sendMessage(
            @RequestBody Message message
    ) {

        return msgService.saveMessage(message);

    }
    
    @GetMapping("/{chatId}")
    public List<Message> getMessages(@PathVariable String chatId){
    	return msgService.getMessagesByChatId(chatId);
    }
    
    @PutMapping("/seen/{chatId}/{receiverId}")
    public void markMessage(@PathVariable String chatId,@PathVariable Long receiverId) {
    	msgService.markMessagesAsSeen(chatId, receiverId);
    	
    }
    
    @GetMapping("/unread/{chatId}/{receiverId}")
    public long getUnreadCount(@PathVariable String chatId,@PathVariable Long receiverId) {
    	return msgService.getUnreadCount(chatId,receiverId);
    }
    
    @GetMapping("/dashboard/{currentUserId}")
    public List<ChatPreviewDTO> getChatDashboard(@PathVariable Long currentUserId){
    	return msgService.getChatDashboard(currentUserId);
    }

}