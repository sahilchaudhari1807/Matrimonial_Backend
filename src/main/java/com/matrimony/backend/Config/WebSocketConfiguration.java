package com.matrimony.backend.Config;

import org.springframework.context.annotation.Configuration;


import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.config.ChannelRegistration;
@Configuration
@EnableWebSocketMessageBroker 
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer{

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
	//	registry.addEndpoint("/ws").withSockJS();
		registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
		
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// TODO Auto-generated method stub
		registry.setApplicationDestinationPrefixes("/app");
		 registry.enableSimpleBroker("/topic");
	}
	@Bean
	public JwtChannelInterceptor jwtChannelInterceptor() {
	    return new JwtChannelInterceptor();
	}
	
	@Override
	public void configureClientInboundChannel(
	        ChannelRegistration registration) {

	    registration.interceptors(jwtChannelInterceptor());
	}

}
