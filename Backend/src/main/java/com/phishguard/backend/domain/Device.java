package com.phishguard.backend.domain;

import com.phishguard.backend.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB 내부 관리용 번호

    // 안드로이드 ID (중복되면 안 됨)
    @Column(nullable = false, unique = true, length = 64)
    private String uuid;

    // 마지막으로 앱을 켠 시간 (활동 로그용)
    private LocalDateTime lastActiveAt;

    // 생성자: 처음 등록할 때 씀
    public Device(String uuid) {
        this.uuid = uuid;
        this.lastActiveAt = LocalDateTime.now();
    }

    // 활동 시간 갱신 메서드: 문자를 검사할 때마다 호출
    public void updateActivity() {
        this.lastActiveAt = LocalDateTime.now();
    }
}
