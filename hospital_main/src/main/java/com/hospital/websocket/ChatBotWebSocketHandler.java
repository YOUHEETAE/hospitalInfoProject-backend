package com.hospital.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.dto.ChatbotResponse;
import com.hospital.service.ChatbotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatBotWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final ChatbotService chatbotService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ChatBotWebSocketHandler(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("ChatBot WebSocket 연결됨: " + session.getId() + ", 총 연결수: " + sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode node = objectMapper.readTree(message.getPayload());

        if (node.has("type") && "chat".equals(node.get("type").asText()) && node.has("message")) {
            String userMessage = node.get("message").asText();

            // 서비스에서 검증 + AI 호출 + 오류 처리
            ChatbotResponse response = chatbotService.chat(userMessage);

            // 응답 전송
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        } else {
            // 알 수 없는 메시지는 서비스에서 처리하도록 로그만 남김
            System.out.println("알 수 없는 메시지 형식 수신: " + message.getPayload());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("ChatBot WebSocket 연결 종료: " + session.getId());
    }
}
