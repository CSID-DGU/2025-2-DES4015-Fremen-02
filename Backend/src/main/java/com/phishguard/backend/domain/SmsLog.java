package com.phishguard.backend.domain;

import com.phishguard.backend.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmsLog extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1 관계 (한 기기가 여러 문자를 검사함)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    // 발신자 번호
    @Column(nullable = false, length = 20)
    private String senderPhone;

    // 문자 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // --- AI 분석 결과 ---
    private Boolean isDanger;      // true(위험), false(안전)
    private String riskLevel;      // HIGH, MEDIUM, SAFE
    private Integer riskScore;     // 0 ~ 100
    private String category;       // GAMBLING, IMPERSONATION, NORMAL 등

    // AI가 생성한 설명
    @Column(columnDefinition = "TEXT")
    private String aiReason;

    @Builder
    public SmsLog(Device device, String senderPhone, String content,
                  Boolean isDanger, String riskLevel, Integer riskScore,
                  String category, String aiReason) {
        this.device = device;
        this.senderPhone = senderPhone;
        this.content = content;
        this.isDanger = isDanger;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.category = category;
        this.aiReason = aiReason;
    }

    public void updateAiResult(boolean isDanger, String riskLevel, int riskScore, String category, String aiReason) {
        this.isDanger = isDanger;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.category = category;
        this.aiReason = aiReason;
    }
}
