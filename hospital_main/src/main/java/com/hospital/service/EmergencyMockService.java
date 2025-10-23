package com.hospital.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.dto.EmergencyApiResponse;
import com.hospital.dto.EmergencyWebResponse;
import com.hospital.mock.EmergencyMockDataGenerator;
import com.hospital.websocket.EmergencyApiWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class EmergencyMockService {

	private final EmergencyMockDataGenerator mockDataGenerator;
	private final ObjectMapper objectMapper;
	private volatile String latestEmergencyJson = null;

	@Autowired
	public EmergencyMockService(EmergencyMockDataGenerator mockDataGenerator) {
		this.mockDataGenerator = mockDataGenerator;
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * Mock 데이터 캐시 업데이트 (강제 재생성)
	 */
	private void updateMockDataCache() {
		try {
			// 강제로 새 데이터 생성
			mockDataGenerator.generateRandomEmergencyData();
			// 생성된 데이터 조회
			List<EmergencyWebResponse> mockData = mockDataGenerator.getCachedEmergencyData();
			updateCacheFromMockResults(mockData);
			System.out.println("🔧 Mock 응급실 데이터 업데이트 완료: " + mockData.size() + "건");
		} catch (Exception e) {
			System.err.println("Mock 응급실 데이터 처리 중 오류 발생");
			e.printStackTrace();
		}
	}

	/**
	 * Mock WebSocket 연결 시 호출 - 초기 데이터 로드만 수행
	 */
	public void onMockWebSocketConnected() {
		System.out.println("🔧 Mock 모드 활성화 - Mock 데이터 사용");
		// 초기 데이터 로드
		updateMockDataCache();
	}

	/**
	 * Mock 데이터를 캐시에 저장
	 */
	public void updateCacheFromMockResults(List<EmergencyWebResponse> dtoList) {
		if (dtoList == null || dtoList.isEmpty()) {
			return;
		}

		try {
			String newJsonData = objectMapper.writeValueAsString(dtoList);

			if (!newJsonData.equals(latestEmergencyJson)) {
				latestEmergencyJson = newJsonData;
				System.out.println("✅ Mock 응급실 데이터 업데이트 완료");
			}
		} catch (Exception e) {
			System.err.println("Mock 응급실 데이터 처리 중 오류 발생");
			e.printStackTrace();
		}
	}

	/**
	 * Mock WebSocket 초기 연결 시 캐시 반환
	 */
	public JsonNode getMockEmergencyRoomData() {

		if (latestEmergencyJson == null) {
			// 초기 데이터가 없으면 즉시 생성
			updateMockDataCache();

			if (latestEmergencyJson == null) {
				return objectMapper.createObjectNode();
			}
		}

		try {
			return objectMapper.readTree(latestEmergencyJson);
		} catch (Exception e) {
			System.err.println("Mock 응급실 데이터 파싱 중 오류 발생");
			e.printStackTrace();
			return objectMapper.createObjectNode();
		}
	}

	/**
	 * Mock 스케줄러 강제 중지 (WebSocket 연결 종료 시 호출)
	 */
	public void stopMockScheduler() {
		System.out.println("✅ Mock 응급실 스케줄러 중지 요청 (실제 스케줄러는 WebSocketHandler에서 관리)");
	}

	public void forceUpdateData() {
		updateMockDataCache();
	}

	/**
	 * Mock 데이터 즉시 조회 (캐시 무시)
	 */
	public List<EmergencyWebResponse> getMockDataDirect() {
		return mockDataGenerator.getCachedEmergencyData();
	}

	/**
	 * Mock 데이터 총 개수 조회
	 */
	public int getMockDataCount() {
		return mockDataGenerator.getCachedEmergencyData().size();
	}
}
