package com.matrimony.backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.matrimony.backend.Service.JWTservice;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration
        implements WebSocketMessageBrokerConfigurer {

    private final JWTservice jwtService;

    // Constructor injection
    public WebSocketConfiguration(JWTservice jwtService) {
        this.jwtService = jwtService;
    }

    // =====================================================
    // WEBSOCKET ENDPOINT
    // =====================================================

    @Override
    public void registerStompEndpoints(
            StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    // =====================================================
    // MESSAGE BROKER
    // =====================================================

    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry registry) {

        registry.setApplicationDestinationPrefixes("/app");

        registry.enableSimpleBroker("/topic");
    }

    // =====================================================
    // JWT INTERCEPTOR
    // =====================================================

    @Bean
    public JwtChannelInterceptor jwtChannelInterceptor() {

        return new JwtChannelInterceptor(jwtService);
    }

    // =====================================================
    // INBOUND CHANNEL
    // =====================================================

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {

        registration.interceptors(
                jwtChannelInterceptor()
        );
    }
}