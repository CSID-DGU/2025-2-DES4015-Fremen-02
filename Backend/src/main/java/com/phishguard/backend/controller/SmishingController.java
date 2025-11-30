package com.phishguard.backend.controller;

import com.phishguard.backend.dto.AiResultDto;
import com.phishguard.backend.dto.SmsRequestDto;
import com.phishguard.backend.global.dto.ApiResponseDto;
import com.phishguard.backend.service.SmishingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "스미싱 분석 API", description = "문자 내용을 분석하여 스미싱 여부를 판별합니다.")
@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
public class SmishingController {

    private final SmishingService smishingService;

    @Operation(summary = "문자 스미싱 위험도 분석", description = "OCR 처리된 문자 텍스트를 받아 AI 분석 결과를 반환합니다.")
    @PostMapping("/check")
    public ResponseEntity<ApiResponseDto<AiResultDto>> checkSms(@RequestBody SmsRequestDto requestDto) {

        // 1. 서비스에게 일 시키기 (DB 저장 + AI 분석)
        AiResultDto result = smishingService.analyzeMessage(requestDto);

        // 2. 결과 받아서 포장(ApiResponseDto)해서 반환
        return ResponseEntity.ok(ApiResponseDto.success(result));
    }

    // 서버 살아있는지 확인용 (선택)
    @Operation(summary = "서버 상태 확인", description = "서버가 정상 작동 중인지 확인합니다.")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Smishing Server is Running! 🚀");
    }
}
