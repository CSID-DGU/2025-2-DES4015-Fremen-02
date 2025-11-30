package com.phishguard.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString // 로그 찍을 때 편하려고 추가
public class SmsRequestDto {

    // 안드로이드 기기 식별값
    private String deviceUuid;

    // 발신자 번호 (010-1234-5678)
    private String sender;

    // 문자 내용 (OCR 전처리된 텍스트)
    private String content;

    // 문자 수신 시간 (앱에서 보내줌)
    // JSON 형식이 "2025-11-30T14:00:00" 형태여야 자동으로 매핑됨
    private LocalDateTime receivedAt;
}
