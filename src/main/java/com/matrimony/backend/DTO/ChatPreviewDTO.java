package com.matrimony.backend.DTO;

public class ChatPreviewDTO {

    private String chatId;
    private Long otherUserId;
    private String username;
    private String lastMessage;
    private Long lastMessageTime;
    private long unreadCount;

    public ChatPreviewDTO() {
    }

    public ChatPreviewDTO(
            String chatId,
            Long otherUserId,
            String username,
            String lastMessage,
            Long lastMessageTime,
            long unreadCount) {

        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.username = username;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(Long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}