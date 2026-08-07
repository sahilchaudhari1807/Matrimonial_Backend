package com.matrimony.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.matrimony.backend.Model.Message;
import com.matrimony.backend.Service.MessageService;

@Controller
public class ChatController {

    private final MessageService msgService;

    public ChatController(MessageService msgService) {
        this.msgService = msgService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message receiveMessage(@Payload Message message) {
    	
    	 Message saved = msgService.saveMessage(message);

    /*	 System.out.println("Id: " + saved.getId());
    	 System.out.println("ChatId: " + saved.getChatId());
    	 System.out.println("Sender: " + saved.getSenderId());
    	 System.out.println("Receiver: " + saved.getReceiverId());
    	 System.out.println("Content: " + saved.getContent());
    	 System.out.println("Timestamp: " + saved.getTimestamp());
    	 System.out.println("Seen: " + saved.isSeen());   // or getSeen()*/

        System.out.println("========= Message Received =========");
        System.out.println("Sender: " + message.getSenderId());
        System.out.println("Receiver: " + message.getReceiverId());
        System.out.println("Content: " + message.getContent());
        System.out.println("Time: " + message.getTimestamp());
        System.out.println("====================================");
        
        

       

        return saved;
    }
}