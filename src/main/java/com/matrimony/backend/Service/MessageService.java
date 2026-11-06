
package com.matrimony.backend.Service;

import java.util.List;
import java.util.Map;
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
import com.matrimony.backend.exception.ForbiddenException;
import com.matrimony.backend.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageService {

    private final MessageRepo msgRepo;
    private final UserRepo userRepo;
    private final InterestService interestService;
    private final SimpMessagingTemplate messagingTemplate;

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

    // =====================================================
    // Get all messages of a chat
    // =====================================================

    public List<Message> getMessagesByChatId(
            String chatId, Long authenticatedUserId) {

        log.debug("Fetching messages for chat ID: {}", chatId);

        if (chatId == null || !chatId.matches("\\d+_\\d+")) {
            log.warn("Invalid chat ID format");
            throw new IllegalArgumentException("Invalid chat ID");
        }

        String[] parts = chatId.split("_");

        Long user1 = Long.parseLong(parts[0]);
        Long user2 = Long.parseLong(parts[1]);

        if (!authenticatedUserId.equals(user1)
                && !authenticatedUserId.equals(user2)) {

            log.warn("User {} attempted to access chat {} without permission",
                    authenticatedUserId, chatId);

            throw new ForbiddenException("You cannot access another user's chat");
        }

        List<Message> messages =
                msgRepo.findByChatIdOrderByTimestampAsc(chatId);

        log.debug("Retrieved {} messages for chat ID: {}",
                messages.size(), chatId);

        return messages;
    }

    // =====================================================
    // Mark unread messages as seen
    // =====================================================

    public void markMessagesAsSeen(String chatId, Long receiverId) {

        log.debug("Marking unread messages as seen for chat: {}", chatId);

        if (chatId == null || !chatId.matches("\\d+_\\d+")) {
            log.warn("Invalid chat ID format");
            throw new IllegalArgumentException("Invalid chat ID");
        }

        String[] parts = chatId.split("_");

        Long user1 = Long.parseLong(parts[0]);
        Long user2 = Long.parseLong(parts[1]);

        if (!receiverId.equals(user1) && !receiverId.equals(user2)) {

            log.warn("User {} attempted to mark another user's chat as seen",
                    receiverId);

            throw new ForbiddenException(
                    "You cannot access another user's chat");
        }

        List<Message> messages =
                msgRepo.findByChatIdAndReceiverIdAndSeenFalse(
                        chatId, receiverId);

        log.debug("Found {} unread messages in chat {}",
                messages.size(), chatId);

        for (Message msg : messages) {
            msg.setDelivered(true);
            msg.setSeen(true);
        }

        msgRepo.saveAll(messages);

        log.info("{} messages marked as seen in chat {}",
                messages.size(), chatId);

        messagingTemplate.convertAndSend("/topic/seen", messages);

        log.debug("Seen notification published for chat: {}", chatId);
    }

    // =====================================================
    // Mark message as delivered
    // =====================================================

    public void markMessageAsDelivered(
            Long messageId, Long authenticatedUserId) {

        log.debug("Marking message {} as delivered", messageId);

        Message message = msgRepo.findById(messageId)
                .orElseThrow(() -> {
                    log.debug("Message {} not found", messageId);
                    return new ResourceNotFoundException("Message not found");
                });

        if (!authenticatedUserId.equals(message.getReceiverId())) {

            log.warn("User {} attempted to mark message {} as delivered but is not the receiver",
                    authenticatedUserId, messageId);

            throw new ForbiddenException(
                    "You cannot mark another user's message as delivered");
        }

        log.debug("Receiver validation successful for message {}",
                messageId);

        message.setDelivered(true);

        Message updatedMessage = msgRepo.save(message);

        log.info("Message {} marked as delivered", messageId);

        messagingTemplate.convertAndSend(
                "/topic/delivered", updatedMessage);

        log.debug("Delivery notification published for message {}",
                messageId);
    }

    // =====================================================
    // Get unread message count for a chat
    // =====================================================

    public long getUnreadCount(String chatId, Long receiverId) {

        log.debug("Calculating unread count for chat: {}", chatId);

        if (chatId == null || !chatId.matches("\\d+_\\d+")) {
            log.warn("Invalid chat ID format");
            throw new IllegalArgumentException("Invalid chat ID");
        }

        String[] parts = chatId.split("_");

        Long user1 = Long.parseLong(parts[0]);
        Long user2 = Long.parseLong(parts[1]);

        if (!receiverId.equals(user1) && !receiverId.equals(user2)) {

            log.warn("User {} attempted to access unread count of another user's chat",
                    receiverId);

            throw new ForbiddenException(
                    "You cannot access another user's chat");
        }

        long count =
                msgRepo.countByChatIdAndReceiverIdAndSeenFalse(
                        chatId, receiverId);

        log.debug("Unread count for chat {} is {}",
                chatId, count);

        return count;
    }

    // =====================================================
    // Build dashboard chat list
    // =====================================================

    public List<ChatPreviewDTO> getChatDashboard(Long currentUserId) {

        log.debug("Building chat dashboard for user: {}", currentUserId);

        List<Message> allMessages =
                msgRepo.findBySenderIdOrReceiverId(
                        currentUserId, currentUserId);

        log.debug("Retrieved {} messages for dashboard user: {}",
                allMessages.size(), currentUserId);

        Map<String, Message> latestMessage = new HashMap<>();

        for (Message msg : allMessages) {

            String chatId = msg.getChatId();

            if (!latestMessage.containsKey(chatId)) {
                latestMessage.put(chatId, msg);
            } else {
                Message oldMessage = latestMessage.get(chatId);

                if (msg.getTimestamp().isAfter(oldMessage.getTimestamp())) {
                    latestMessage.put(chatId, msg);
                }
            }
        }

        List<ChatPreviewDTO> dashboard = new ArrayList<>();

        for (Message latestMsg : latestMessage.values()) {

            String[] parts = latestMsg.getChatId().split("_");

            Long otherUserId;

            if (Long.parseLong(parts[0]) == currentUserId.longValue()) {
                otherUserId = Long.parseLong(parts[1]);
            } else {
                otherUserId = Long.parseLong(parts[0]);
            }

            long unreadCount =
                    getUnreadCount(latestMsg.getChatId(), currentUserId);

            Users user = userRepo.findById(otherUserId)
                    .orElseThrow(() -> {
                        log.warn("User {} not found while building chat dashboard",
                                otherUserId);
                        return new ResourceNotFoundException("User not found");
                    });

            String username = user.getUsername();

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

        log.info("Chat dashboard built successfully for user {} with {} conversations",
                currentUserId, dashboard.size());

        return dashboard;
    }

    // =====================================================
    // Save message
    // =====================================================

    public Message saveMessage(Message message) {

        Long senderId = message.getSenderId();
        Long receiverId = message.getReceiverId();

        log.debug("Processing message from user {} to user {}",
                senderId, receiverId);

        if (!interestService.isMatched(senderId, receiverId)) {

            log.warn("Message rejected: users {} and {} are not matched",
                    senderId, receiverId);

            throw new ForbiddenException(
                    "Only matched users can chat");
        }

        log.debug("Match validation successful for users {} and {}",
                senderId, receiverId);

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

        Message savedMessage = msgRepo.save(message);

        log.info("Message {} saved successfully in chat {}",
                savedMessage.getId(), chatId);

        return savedMessage;
    }
}
