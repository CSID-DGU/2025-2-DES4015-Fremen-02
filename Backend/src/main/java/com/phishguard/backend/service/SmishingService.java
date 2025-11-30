package com.phishguard.backend.service;

import com.phishguard.backend.domain.Device;
import com.phishguard.backend.domain.SmsLog;
import com.phishguard.backend.dto.AiResultDto;
import com.phishguard.backend.dto.SmsRequestDto;
import com.phishguard.backend.global.exception.CustomException;
import com.phishguard.backend.global.exception.ErrorCode;
import com.phishguard.backend.repository.DeviceRepository;
import com.phishguard.backend.repository.SmsLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmishingService {

    private final SmsLogRepository smsLogRepository;
    private final DeviceRepository deviceRepository;
    private final WebClient webClient; // Config에 등록한 그 녀석

    /**
     * 핵심 로직: 문자 수신 -> 기기확인 -> AI분석 -> 결과저장 -> 반환
     */
    @Transactional
    public AiResultDto analyzeMessage(SmsRequestDto requestDto) {

        // 1. 기기 정보 확인 (없으면 등록, 있으면 시간 업데이트)
        Device device = deviceRepository.findByUuid(requestDto.getDeviceUuid())
                .orElseGet(() -> deviceRepository.save(new Device(requestDto.getDeviceUuid())));
        device.updateActivity(); // 마지막 활동 시간 갱신

        // 2. 일단 로그 저장 (AI 결과는 비워둔 채로)
        // 혹시 AI 서버가 죽더라도 "문자가 왔다"는 기록은 남기기 위함
        SmsLog smsLog = SmsLog.builder()
                .device(device)
                .senderPhone(requestDto.getSender())
                .content(requestDto.getContent())
                .isDanger(false) // 기본값
                .build();

        smsLogRepository.save(smsLog); // DB에 INSERT

        // 3. AI 서버로 요청 보내기 (WebClient 사용)
        AiResultDto aiResult;
        try {
            // 파이썬 서버가 받을 JSON 형태: {"text": "문자내용..."}
            Map<String, String> aiRequest = new HashMap<>();
            aiRequest.put("text", requestDto.getContent());

            aiResult = webClient.post()
                    .uri("/predict") // 파이썬 서버의 엔드포인트 (AI팀과 맞춰야 함!)
                    .bodyValue(aiRequest)
                    .retrieve()
                    .bodyToMono(AiResultDto.class) // 응답을 이 객체로 변환
                    .block(); // 결과 올 때까지 기다림 (Sync)

        } catch (Exception e) {
            log.error("AI 서버 통신 오류: {}", e.getMessage());
            // GlobalExceptionHandler가 이걸 잡아서 "ERR_SERVER_ERROR" JSON을 만들어줍니다.
            throw new CustomException(ErrorCode.AI_SERVER_ERROR);
        }

        // 4. AI 분석 결과를 아까 저장한 로그에 업데이트
        if (aiResult != null) {
            smsLog.updateAiResult(
                    aiResult.isDanger(),
                    aiResult.getRiskLevel(),
                    aiResult.getRiskScore(),
                    aiResult.getCategory(),
                    aiResult.getReason()
            );
        }

        return aiResult;
    }
}
