package com.matrimony.backend.DTO;

public class TypingStatus {
	private Long senderId;
	private Long receiverId;
	private boolean typing;

	// Getter and Setter for senderId
	public Long getSenderId() {
	    return senderId;
	}

	public void setSenderId(Long senderId) {
	    this.senderId = senderId;
	}

	// Getter and Setter for receiverId
	public Long getReceiverId() {
	    return receiverId;
	}

	public void setReceiverId(Long receiverId) {
	    this.receiverId = receiverId;
	}

	// Getter and Setter for typing
	public boolean isTyping() {
	    return typing;
	}

	public void setTyping(boolean typing) {
	    this.typing = typing;
	}
}
