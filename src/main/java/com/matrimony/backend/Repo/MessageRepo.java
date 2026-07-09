package com.matrimony.backend.Repo;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.matrimony.backend.Model.Message;

public interface MessageRepo
extends JpaRepository<Message, Long> {

List<Message> findByChatIdOrderByTimestampAsc(
     String chatId
);

List<Message>
findByChatIdAndReceiverIdAndSeenFalse(
        String chatId,
        Long receiverId
);

long countByChatIdAndReceiverIdAndSeenFalse(String ChatId,Long receiverId) ;
	
List<Message> findBySenderIdOrReceiverId(Long senderId,Long receiverId);

}