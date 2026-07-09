package com.matrimony.backend.Service;

import java.util.List;


import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;





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
    public MessageService(MessageRepo msgRepo,UserRepo userRepo,InterestService interestService) {
    	this.msgRepo=msgRepo;
    	this.userRepo=userRepo;
    	this.interestService=interestService;
    }
  
    // =====================================================
    // Save new message
    // Only matched users are allowed to chat
    // =====================================================
    public Message saveMessage(Message message) {

    	
    	
        // 🔹 Add timestamp
    	 if(interestService.isMatched(message.getSenderId(),message.getReceiverId())) {
    	
    		
        message.setTimestamp(
                System.currentTimeMillis()
        );
       
        // 🔹 New message unseen
        message.setSeen(false);

        // 🔹 Save into database
        return msgRepo.save(message);
    	 }else {
    		 throw new RuntimeException("only matched users can chat");
    	 }
    }

    // =====================================================
    // Get all messages of a chat
    // Sorted by oldest to newest
    // =====================================================
	public List<Message> getMessagesByChatId(String chatId ) {
		// TODO Auto-generated method stub
		
		return msgRepo.findByChatIdOrderByTimestampAsc(chatId);
	}
	
	 // =====================================================
    // Mark unread messages as seen
    // Called when receiver opens chat
    // =====================================================
	public void markMessagesAsSeen(String chatId,Long receiverId){
		 List<Message> messages =
		            msgRepo
		            .findByChatIdAndReceiverIdAndSeenFalse(
		                    chatId,
		                    receiverId
		            );

		    // 🔹 Mark all as seen
		    for (Message msg : messages) {

		        msg.setSeen(true);

		    }

		    // 🔹 Save updated messages
		    msgRepo.saveAll(messages);
	}
	
	// =====================================================
    // Get unread message count for a chat
    // =====================================================
	public long getUnreadCount(String chatId,Long receiverId) {
		return msgRepo.countByChatIdAndReceiverIdAndSeenFalse(chatId,receiverId);
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
				
				if(msg.getTimestamp()>old_message.getTimestamp()) {
					latestMessage.put(chat_id, msg);
				}
			}
		}
			
		
		 List<ChatPreviewDTO> dashboard =new ArrayList<>();
		
		for (Message latestMsg : latestMessage.values()) {
			String[] parts = latestMsg.getChatId().split("_");
			
			Long otherUserId;

			if (Long.parseLong(parts[0]) == currentUserId) {
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
		        latestMsg.getText(),
		        latestMsg.getTimestamp(),
		        unreadCount
		);

		 dashboard.add(dto);
		}
		return dashboard;
	}
}