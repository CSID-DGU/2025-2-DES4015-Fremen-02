package com.phishguard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResultDto {

    // 위험 여부 (true: 위험/의심, false: 안전)
    private boolean isDanger;

    // 위험 등급 (HIGH, MEDIUM, SAFE)
    private String riskLevel;

    // AI 확신도 (0 ~ 100점)
    private int riskScore;

    // 스미싱 유형 (GAMBLING: 도박, IMPERSONATION: 사칭, NORMAL: 정상)
    private String category;

    // AI가 생성한 상세 설명 ("자녀 사칭 패턴이 감지되었습니다.")
    private String reason;
}
