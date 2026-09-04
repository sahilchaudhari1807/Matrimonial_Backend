package com.matrimony.backend.Service;

import java.util.List;


import java.util.Map;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import com.matrimony.backend.DTO.ChatPreviewDTO;
import com.matrimony.backend.Model.Message;
import com.matrimony.backend.Model.Users;
import com.matrimony.backend.Repo.MessageRepo;
import com.matrimony.backend.Repo.UserRepo;



@Service
public class MessageService {

   
    private final MessageRepo msgRepo;
    private final UserRepo userRepo;
    private final InterestService interestService;
    private SimpMessagingTemplate messagingTemplate;
    public MessageService(
            MessageRepo msgRepo,
            UserRepo userRepo,
            InterestService interestService,
            SimpMessagingTemplate messagingTemplate) {

        this.msgRepo = msgRepo;
        this.userRepo = userRepo;
        this.interestService = interestService;
        this.messagingTemplate = messagingTemplate;
    }
   

    // Constructor Injection
   
   

    // =====================================================
    // Get all messages of a chat
    // Sorted by oldest to newest
    // =====================================================
	public List<Message> getMessagesByChatId(String chatId ,Long authenticatedUserId) {
		// TODO Auto-generated method stub
		String parts[]=chatId.split("_");
		
		Long user1=Long.parseLong(parts[0]);
		Long user2=Long.parseLong(parts[1]);
		
		if(!authenticatedUserId.equals(user1) && !authenticatedUserId.equals(user2)) {
			throw new RuntimeException("you cannot acces others chat");
		}
		return msgRepo.findByChatIdOrderByTimestampAsc(chatId);
	}
	
	 // =====================================================
    // Mark unread messages as seen
    // Called when receiver opens chat
    // =====================================================
	public void markMessagesAsSeen(String chatId, Long receiverId) {
		
		 String[] parts = chatId.split("_");

		    Long user1 = Long.parseLong(parts[0]);
		    Long user2 = Long.parseLong(parts[1]);

		    if (!receiverId.equals(user1) && !receiverId.equals(user2)) {

		        throw new RuntimeException(
		                "You cannot access another user's chat"
		        );
		    }

	    // 1. Find all unseen messages for this receiver
	//	System.out.println("markMessagesAsSeen called");
		
		
	    List<Message> messages =
	            msgRepo.findByChatIdAndReceiverIdAndSeenFalse(
	                    chatId,
	                    receiverId
	            );
	 

	    // 2. Mark each message as seen
	    for (Message msg : messages) {
	        msg.setSeen(true);
	    }

	    // 3. Save changes to database
	    msgRepo.saveAll(messages);
	   // System.out.println("Saved successfully");
	   
	    System.out.println("Broadcasting seen update");
	    
	  
	    messagingTemplate.convertAndSend("/topic/seen", messages);
	}
	
	
	public void markMessageAsDelivered(
	        Long messageId,
	        Long authenticatedUserId) {

	    System.out.println("markMessageAsDelivered called");
	    System.out.println("Message ID: " + messageId);

	    Message message = msgRepo.findById(messageId)
	            .orElseThrow(() ->
	                    new RuntimeException("Message not found"));

	    // Only receiver can mark message as delivered
	    if (!authenticatedUserId.equals(message.getReceiverId())) {

	        throw new RuntimeException(
	                "You cannot mark another user's message as delivered"
	        );
	    }

	    System.out.println(
	            "Before delivered: " + message.isDelivered()
	    );

	    message.setDelivered(true);

	    Message updatedMessage = msgRepo.save(message);

	    System.out.println(
	            "After delivered: " + updatedMessage.isDelivered()
	    );

	    messagingTemplate.convertAndSend(
	            "/topic/delivered",
	            updatedMessage
	    );
	}
	// =====================================================
    // Get unread message count for a chat
    // =====================================================
	public long getUnreadCount(String chatId, Long receiverId) {

	    // Check that receiver is part of this chat
	    String[] parts = chatId.split("_");

	    Long user1 = Long.parseLong(parts[0]);
	    Long user2 = Long.parseLong(parts[1]);

	    if (!receiverId.equals(user1) && !receiverId.equals(user2)) {
	        throw new RuntimeException(
	                "You cannot access another user's chat"
	        );
	    }

	    return msgRepo.countByChatIdAndReceiverIdAndSeenFalse(
	            chatId,
	            receiverId
	    );
	}

	  // =====================================================
    // Build dashboard chat list
    // Returns:
    // - last message
    // - username
    // - unread count
    // - timestamp
    // =====================================================
	public List<ChatPreviewDTO> getChatDashboard(Long currentUserId){
		List<Message> allMessages=msgRepo.findBySenderIdOrReceiverId(currentUserId,currentUserId);
		
		Map<String,Message> latestMessage=new HashMap<>();
		
		for(Message msg:allMessages) {
			String chat_id=msg.getChatId();
			
			if(!latestMessage.containsKey(chat_id)) {
				 latestMessage.put(chat_id,msg);
			}
			else {
				Message old_message=latestMessage.get(chat_id);
				
				 if (msg.getTimestamp().isAfter(old_message.getTimestamp())) {
	                    latestMessage.put(chat_id, msg);
	                }
			}
		}
			
		
		 List<ChatPreviewDTO> dashboard =new ArrayList<>();
		
		for (Message latestMsg : latestMessage.values()) {
			String[] parts = latestMsg.getChatId().split("_");
			
			Long otherUserId;

			if (Long.parseLong(parts[0]) == currentUserId.longValue()) {
			    otherUserId = Long.parseLong(parts[1]);
			} else {
			    otherUserId = Long.parseLong(parts[0]);
			}
		
		
		long unreadCount=getUnreadCount(latestMsg.getChatId(),currentUserId);
		Users user=userRepo.findById(otherUserId).orElse(null);
		
		String username =
		        user != null
		        ? user.getUsername()
		        : "Unknown";
		
		ChatPreviewDTO dto = new ChatPreviewDTO(
		        latestMsg.getChatId(),
		        otherUserId,
		        username,
		        latestMsg.getContent(),
		        latestMsg.getTimestamp(),
		        unreadCount
		);

		 dashboard.add(dto);
		}
		return dashboard;
	}
	
	public Message saveMessage(Message message) {

	    if (!interestService.isMatched(
	            message.getSenderId(),
	            message.getReceiverId())) {

	        throw new RuntimeException("Only matched users can chat");
	    }

	    Long senderId = message.getSenderId();
	    Long receiverId = message.getReceiverId();

	    String chatId;

	    if (senderId < receiverId) {
	        chatId = senderId + "_" + receiverId;
	    } else {
	        chatId = receiverId + "_" + senderId;
	    }

	    message.setChatId(chatId);

	    message.setTimestamp(LocalDateTime.now());
	    
	    message.setDelivered(false);
	    message.setSeen(false);

	    return msgRepo.save(message);
	}
}