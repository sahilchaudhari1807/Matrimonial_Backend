package com.matrimony.backend.DTO;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class ChatMessage {

	private Long senderId;
	private Long receiverId;
	private String content;
	private LocalDateTime timestamp;
}
