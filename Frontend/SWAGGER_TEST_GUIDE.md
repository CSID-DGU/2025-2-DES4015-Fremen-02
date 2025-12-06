# Swagger API 테스트 가이드

## 📍 서버 정보

**Base URL**: `http://43.200.113.167:8080`

**엔드포인트**: `/api/v1/sms/check`

**전체 URL**: `http://43.200.113.167:8080/api/v1/sms/check`

---

## 🔧 요청 정보

### HTTP Method
```
POST
```

### Headers
```
Content-Type: application/json
Accept: application/json
```

### Request Body (JSON)

```json
{
  "deviceUuid": "android_id_example_12345",
  "sender": "010-1234-5678",
  "content": "안녕하세요! Casino 이벤트 참여시 100% 보장! 지금 https://casino.xyz 클릭하세요!",
  "received_at": "2025-12-01T03:30:00"
}
```

### 필드 설명

| 필드명 | 타입 | 필수 | 설명 | 예시 |
|--------|------|------|------|------|
| `deviceUuid` | String | ✅ | 기기 식별값 (Android ID) | `"android_id_example_12345"` |
| `sender` | String | ✅ | 문자를 보낸 발신 번호 | `"010-1234-5678"` |
| `content` | String | ✅ | OCR 및 특수문자 제거 후 정제된 텍스트 (마스킹 처리됨) | `"안녕하세요! Casino 이벤트..."` |
| `received_at` | String | ✅ | 문자가 수신된 시간 (ISO 8601 형식) | `"2025-12-01T03:30:00"` |

---

## 📥 응답 정보

### 성공 응답 (200 OK)

```json
{
  "status": "success",
  "data": {
    "is_danger": true,
    "risk_level": "HIGH",
    "risk_score": 97,
    "category": "GAMBLING",
    "reason": "도박 키워드 + 과장문구(100% 보장) + 고위험 도메인(.xyz) 패턴 감지"
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "code": null
}
```

**참고**: 실제 서버 응답에서는 `status`가 `null`일 수 있고, `message`에 "요청이 성공적으로 처리되었습니다."가 포함될 수 있습니다.

### 실패 응답 (400 Bad Request)

```json
{
  "status": "error",
  "message": "문자 내용(content)은 필수 입력값입니다.",
  "code": "ERR_INVALID_INPUT",
  "data": null
}
```

### 실패 응답 (500 Internal Server Error)

```json
{
  "status": 500,
  "error": "INTERNAL_SERVER_ERROR",
  "code": "E5001",
  "message": "서버 내부 오류가 발생했습니다.",
  "timestamp": "2025-11-30T18:19:01.236160868"
}
```

---

## 🧪 테스트 예제

### 예제 1: 위험 메시지 (도박)

**Request:**
```json
{
  "deviceUuid": "test_device_001",
  "sender": "010-1234-5678",
  "content": "안녕하세요! Casino 이벤트 참여시 100% 보장! 지금 https://casino.xyz 클릭하세요!",
  "received_at": "2025-12-01T10:00:00"
}
```

**Expected Response:**
```json
{
  "status": "success",
  "data": {
    "is_danger": true,
    "risk_level": "HIGH",
    "risk_score": 97,
    "category": "GAMBLING",
    "reason": "도박 키워드 감지"
  }
}
```

### 예제 2: 정상 메시지

**Request:**
```json
{
  "deviceUuid": "test_device_001",
  "sender": "1588-1234",
  "content": "[KB국민카드] 데이터 분석 부트캠프 안내. 최대 170만원 지원금과 인턴십 기회까지 제공합니다.",
  "received_at": "2025-12-01T10:00:00"
}
```

**Expected Response:**
```json
{
  "status": "success",
  "data": {
    "is_danger": false,
    "risk_level": "SAFE",
    "risk_score": 10,
    "category": "NORMAL",
    "reason": "정상 메시지"
  }
}
```

### 예제 3: 사칭 메시지

**Request:**
```json
{
  "deviceUuid": "test_device_001",
  "sender": "1588-1234",
  "content": "KB국민카드입니다. 계좌 확인을 위해 링크를 클릭해주세요. https://kb-card-fake.com",
  "received_at": "2025-12-01T10:00:00"
}
```

**Expected Response:**
```json
{
  "status": "success",
  "data": {
    "is_danger": true,
    "risk_level": "MEDIUM",
    "risk_score": 75,
    "category": "IMPERSONATION",
    "reason": "사칭 의심 패턴 감지"
  }
}
```

---

## 🔍 Swagger에서 테스트하는 방법

1. **Swagger UI 접속**
   - `http://43.200.113.167:8080/swagger-ui.html` (또는 백엔드 팀이 제공한 Swagger 주소)

2. **엔드포인트 선택**
   - `POST /api/v1/sms/check` 선택

3. **Try it out** 클릭

4. **Request Body 입력**
   ```json
   {
     "deviceUuid": "test_device_001",
     "sender": "010-1234-5678",
     "content": "안녕하세요! Casino 이벤트 참여시 100% 보장!",
     "received_at": "2025-12-01T10:00:00"
   }
   ```

5. **Execute** 클릭

6. **응답 확인**

---

## 📝 참고 사항

- **타임아웃**: 180초 (3분)
- **Content-Type**: `application/json` 필수
- **날짜 형식**: ISO 8601 형식 (`YYYY-MM-DDTHH:mm:ss`)
- **deviceUuid**: 실제 Android ID 또는 테스트용 임의 값 사용 가능

---

## 🐛 문제 해결

### 타임아웃 발생 시
- 서버 응답 시간이 3분 이상 소요되는 경우
- 백엔드 팀에 문의하여 AI 서버 응답 시간 확인

### 400 에러 발생 시
- 필수 필드 누락 확인
- JSON 형식 확인
- 필드 이름 확인 (`deviceUuid`, `received_at` 등)

### 500 에러 발생 시
- 서버 내부 오류
- 백엔드 팀에 로그 확인 요청

