package com.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hospital.websocket.EmergencyApiWebSocketHandler;

/**
 * 🔌 WebSocket 관련 설정
 * - 응급실 실시간 데이터 전송용 WebSocket 설정
 * - CORS 설정 포함
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public EmergencyApiWebSocketHandler emergencyApiWebSocketHandler() {
        System.out.println("✅ Emergency WebSocket Handler 생성");
        return new EmergencyApiWebSocketHandler();
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(emergencyApiWebSocketHandler(), "/emergency-websocket")
                .setAllowedOrigins("*");  // 실제 운영에서는 특정 도메인으로 제한 권장
        
        System.out.println("✅ WebSocket 핸들러 등록 완료: /emergency-websocket");
    }
}