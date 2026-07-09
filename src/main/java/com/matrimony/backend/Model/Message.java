package com.matrimony.backend.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity

public class Message {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String chatId;

	    private Long senderId;

	    private Long receiverId;

	    private String text;

	    private Long timestamp;

	    private boolean seen;

	    // =========================
	    // GETTERS AND SETTERS
	    // =========================

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getChatId() {
	        return chatId;
	    }

	    public void setChatId(String chatId) {
	        this.chatId = chatId;
	    }

	    public Long getSenderId() {
	        return senderId;
	    }

	    public void setSenderId(Long senderId) {
	        this.senderId = senderId;
	    }

	    public Long getReceiverId() {
	        return receiverId;
	    }

	    public void setReceiverId(Long receiverId) {
	        this.receiverId = receiverId;
	    }

	    public String getText() {
	        return text;
	    }

	    public void setText(String text) {
	        this.text = text;
	    }

	    public Long getTimestamp() {
	        return timestamp;
	    }

	    public void setTimestamp(Long timestamp) {
	        this.timestamp = timestamp;
	    }

	    public boolean isSeen() {
	        return seen;
	    }

	    public void setSeen(boolean seen) {
	        this.seen = seen;
	    }
}
