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
     * ✅ [추가 기능] 짧고 안전한 문자 필터링 (비용 절감 & 오탐지 방지)
     * - 길이가 5자 미만이면서 숫자나 URL이 없으면 AI 검사 없이 바로 '안전' 처리
     */
    private boolean isSafeShortText(String content) {
        if (content == null) return true;
        String text = content.trim();

        // 길이가 5자 미만이면 일단 의심 안 함 (예: "김밥", "ㅇㅇ", "집이야")
        if (text.length() < 5) {
            // 단, 짧더라도 "숫자"나 "URL"이 있으면 위험할 수 있음
            boolean hasNumber = text.matches(".*\\d.*"); // 숫자가 포함됨?
            boolean hasUrl = text.matches(".*(http|www|com|kr).*"); // URL 패턴?

            // 숫자도 없고, URL도 없으면 -> 진짜 안전한 짧은 문자 (True)
            return !hasNumber && !hasUrl;
        }
        return false; // 5자 이상이면 무조건 AI 검사
    }

    /**
     * 핵심 로직: 문자 수신 -> 기기확인 -> (필터링) -> AI분석 -> 결과저장 -> 반환
     * * 🚨 주의: AI 통신이 오래 걸리므로 @Transactional을 메서드 전체에 걸지 않음 (DB 잠금 해제)
     */
    public AiResultDto analyzeMessage(SmsRequestDto requestDto) {

        // 1. 기기 정보 확인 (없으면 등록, 있으면 시간 업데이트)
        Device device = deviceRepository.findByUuid(requestDto.getDeviceUuid())
                .orElseGet(() -> deviceRepository.save(new Device(requestDto.getDeviceUuid())));
        device.updateActivity(); // 마지막 활동 시간 갱신
        deviceRepository.save(device);

        // 2. 로그 1차 저장 (AI 결과는 비워둔 채로)
        // 혹시 AI 서버가 죽더라도 "문자가 왔다"는 기록은 남기기 위함
        SmsLog smsLog = SmsLog.builder()
                .device(device)
                .senderPhone(requestDto.getSender())
                .content(requestDto.getContent())
                .isDanger(false) // 기본값
                .build();

        smsLogRepository.save(smsLog); // DB에 INSERT

        // =========================================================
        // [최적화] AI한테 보내기 전에 먼저 검사 ("김밥" 같은 거 거르기)
        // =========================================================
        if (isSafeShortText(requestDto.getContent())) {
            log.info("AI 검사 패스: 정보량이 적은 안전한 문자입니다. 내용: {}", requestDto.getContent());

            // AI 안 거치고 바로 안전(SAFE) 결과 리턴
            return AiResultDto.builder()
                    .isDanger(false)
                    .riskLevel("SAFE")
                    .riskScore(0.0) // 점수 0점
                    .category("NORMAL")
                    .reason("정보가 부족하거나 안전한 단문 메시지입니다.")
                    .build();
        }

        // 3. AI 서버로 요청 보내기 (필터 통과한 애들만 실행)
        AiResultDto aiResult;
        try {
            // AI 서버가 받을 JSON 형태: {"text": "문자내용..."}
            Map<String, String> aiRequest = new HashMap<>();
            aiRequest.put("text", requestDto.getContent());

            aiResult = webClient.post()
                    .uri("/api/v1/smishing-rag")
                    .bodyValue(aiRequest)
                    .retrieve()
                    .bodyToMono(AiResultDto.class) // 응답을 이 객체로 변환
                    .block(); // 결과 올 때까지 기다림

            if (aiResult != null) {
                double rawScore = aiResult.getRiskScore(); // AI가 준 원본 점수 (예: 0.98)

                // [수정] 애매한 건 NORMAL로 처리하기 위해 기준을 높임
                if (rawScore >= 0.90) {
                    aiResult.setDanger(true);
                    aiResult.setRiskLevel("CRITICAL"); // 90점 이상: 매우 위험
                } else if (rawScore >= 0.75) {
                    aiResult.setDanger(true);
                    aiResult.setRiskLevel("CAUTION");  // 75~89점: 주의
                } else {
                    aiResult.setDanger(false);
                    aiResult.setRiskLevel("SAFE");     // 75점 미만: 안전
                }

                // 프론트엔드 표시용 점수 변환 (0.98 -> 98.0)
                aiResult.setRiskScore(rawScore * 100);
            }


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
                    (int) (aiResult.getRiskScore()),
                    aiResult.getCategory(),
                    aiResult.getReason()
            );
            smsLogRepository.save(smsLog);
        }

        return aiResult;
    }
}
