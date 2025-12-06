# 🚨 Fremen – LLM 기반 스미싱 탐지·차단 서비스

> **실시간 스미싱 탐지 · 위험 이유 설명 · 즉각적인 대응까지 지원하는 차세대 보안 서비스**

---

## 📌 프로젝트 소개 (Introduction)

**Fremen**은 문자(SMS) 및 메신저로 유입되는 스미싱 메시지를 **실시간으로 탐지하고**, 단순 차단을 넘어
**“왜 위험한지”를 LLM 기반으로 설명해주는 지능형 스미싱 탐지·차단 서비스**입니다.

기존의 패턴·블랙리스트 중심 스미싱 탐지 방식은

* 변형된 문자열(CÅsIΠΌ → Casino),
* 기관·지인 사칭 문장,
* LLM 기반 고도화된 스미싱 메시지

등을 탐지하는 데 한계가 있습니다.

Fremen은 **On-Device AI + Server-Side AI + LLM·RAG 기반 근거 생성**을 조합하여
스미싱 공격을 사전에 차단하고 사용자가 즉시 대응할 수 있는 환경을 제공합니다.

---

## ✨ 핵심 기능 (Key Features)

### 🔎 1) On-Device AI 기반 **변형 키워드 자동 복원**

* “CÅsIΠΌ”, “카.zI.NO”, 세로쓰기 등 **우회 문자열** 탐지
* YOLO 기반 Object Detection → Tesseract OCR → **정상 문자열 복원**

### 🤖 2) Server-Side AI 기반 **스미싱 메시지 정밀 분석**

* KoBERT 모델 기반 7개 카테고리 다중 분류
* (도박 / 대출 / 기관 사칭 / 알바 / 성인 / 통신사기 / 정상)

### 🧠 3) LLM + RAG 기반 **위험 근거 설명(Explainable AI)**

* “이 메시지가 왜 위험한지”를 자연어로 친절하게 설명
* 예:

  > 해당 메시지는 ‘기관 사칭’ 패턴과 유사하며, 포함된 URL이 정상 정부 도메인과 다릅니다.

### ⚡ 4) 실시간 알림 & 즉각적 대응

* 스미싱 감지 시 즉시 푸시 알림
* “번호 차단 · KISA 신고 · 계좌 지급정지 안내” 등 후속 행동 제공

### 🔐 5) 프라이버시 중심 설계 (On-Device First)

* 원본 문자는 서버로 전송되지 않음
* 클라이언트는 “정상화된 텍스트”만 서버로 전달 → 개인정보 보호

---

# 🏛️ 시스템 아키텍처 (Architecture)

```
[User Device]
 ├─ On-Device AI
 │    ├ Text → Image 변환
 │    ├ 변형 키워드 탐지 (YOLO)
 │    ├ OCR 복원 (Tesseract)
 │    └ 1차 전처리 (URL 마스킹 등)
 │
 └→ REST API 전송

[Backend Server]
 ├ Spring Boot API Gateway
 └ 메시지 전달 → AI Server

[AI Server]
 ├ 전처리
 ├ KoBERT 분류 모델
 ├ RAG 기반 위험 근거 생성
 └ 결과 JSON 반환

[Client App]
 └ 위험 알림 + 대응 버튼 제공
```

---

# 📦 설치 및 실행 방법 (Installation)

## 1) Android 앱 설치 (사용자용)

> 실제 배포 시 APK 또는 Play Store 링크 배치

```
준비 중
```

---

## 2) Backend(Spring Boot) 설치

### 🔧 Requirements

* Java 17
* Spring Boot 3.2+
* MySQL 8.0
* Gradle 8+

### 📥 Clone

```bash
git clone https://github.com/CSID-DGU/2025-2-DES4015-Fremen-02.git
cd Backend
```

### ⚙️ 환경 변수 설정 (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fremen
    username: root
    password: yourpassword
```

### ▶️ 실행

```bash
./gradlew bootRun
```

---

## 3) AI 서버 설치 (Python)

### Requirements

```
Python 3.10+
torch 2.x
transformers
sentencepiece
uvicorn
fastapi
```

### Install

```bash
cd AI
pip install -r requirements.txt
```

### Run AI Server

```bash
uvicorn main:app --host 0.0.0.0 --port 8000
```

---

# 🧪 사용 예시 (Usage Examples)

### 📤 클라이언트 → 서버 요청 예시

```json
{
  "deviceUuid": "android_id_example_12345", 
  "sender": "010-1234-5678",
  "content": "안녕하세요! Casino 이벤트 참여시 100% 보장! 지금 <URL> 클릭",
  "received_at": "2025-11-09T19:05:00"
}
```

### 📥 서버 응답 예시

```json
{
  "status": "success",
  "data": {
    "is_danger": true,
    "risk_level": "HIGH",
    "risk_score": 97,
    "category": "GAMBLING",
    "reason": "도박 키워드 + 과장문구(100% 보장) + 고위험 도메인(.xyz) 패턴 감지"
  }
}
```

---

# 🧠 개발환경 (Development Environment)

| 구분                | 기술 스택                                   |
| ----------------- | --------------------------------------- |
| **Frontend(App)** | Kotlin, Android Studio, Jetpack Compose |
| **Backend**       | Java 17, Spring Boot, MySQL, JPA        |
| **AI Server**     | FastAPI, KoBERT, Llama3-Korean, RAG     |
| **On-Device AI**  | TensorFlow Lite, YOLOv5                 |
| **협업 도구**         | GitHub, Notion, Slack         |

---

# 🔍 상세 기술 구성 (Tech Details)

## 1️⃣ On-Device Keyword Recovery Pipeline

1. 수신된 SMS를 텍스트로 받음
2. 기기 내에서 이미지 렌더링
3. YOLO 모델로 변형 문자열 탐지
4. OCR 인식 → Casino 등 정상 문자열 복원
5. 복원된 텍스트만 서버로 전송

→ **프라이버시 보호 + 1차 스팸 방지**

---

## 2️⃣ Server-Side NLP Classification

* KoBERT 기반 7-class 다중 분류
* 학습 데이터: 약 60,000건 스미싱 + 18,000건 일반 메시지
* 클래스:
  `도박, 대출, 기관사칭, 알바사칭, 성인, 통신사기, 정상`

---

## 3️⃣ LLM + RAG 위험 근거 생성

* 실제 스미싱 사례/정책/패턴 데이터베이스 구축
* 검색 기반 위험 사유 추출
* LLM이 자연어로 설명 생성

---

# 📊 성능 (Performance)

| 방식                              | Precision | Recall   | F1-score |
| ------------------------------- | --------- | -------- | -------- |
| 키워드 기반 필터링                      | 0.56      | 0.34     | 0.53     |
| Naive Bayes                     | 0.74      | 0.68     | 0.71     |
| 자연어 처리만 적용                      | 0.83      | 0.76     | 0.79     |
| **Fremen 전체 파이프라인(Python env)** | **0.91**  | **0.95** | **0.93** |
| **Fremen 모바일 환경**               | 0.89      | 0.78     | 0.83     |

> 복원(Recovery) 단계가 성능에 가장 큰 기여.

---

# 👥 팀 소개 (Team)

| 이름           | 역할                       | GitHub                                                       |
| ------------ | ------------------------ | ------------------------------------------------------------ |
| **임현우 (팀장)** | AI 모델링 · AI 통합 · 아키텍처 설계 | [https://github.com/pyeree](https://github.com/pyeree) |
| 윤찬혁          |  백엔드 개발 · 문서 통합           |   [https://github.com/younchanhyeok](https://github.com/younchanhyeok)      |
| 조효동          | 안드로이드 개발 · On-Device AI  |      [https://github.com/hyodongg](https://github.com/hyodongg)        |

---

# 📬 연락처(Contact)

프로젝트 이메일: **[dlagusdn0218@gmail.com](mailto:dlagusdn0218@gmail.com)**
GitHub 저장소: [https://github.com/CSID-DGU/2025-2-DES4015-Fremen-02](https://github.com/CSID-DGU/2025-2-DES4015-Fremen-02)

---

# 📄 참고문헌(논문·기술자료)

본 README에 포함된 기술 배경 및 스팸 필터링 파트는
고려대학교 SW·AI융합대학원 석사 논문 **「모바일 환경에서의 연합학습을 활용한 On-Device AI 스팸문자 필터링 시스템」** 의 내용을 참고함.

프로젝트 수행계획서는 지산학 캡스톤 디자인 보고서 양식 기반으로 작성됨.


---

# 🚀 향후 개발 로드맵 (Roadmap)

* [ ] iOS 버전 개발
* [ ] LLM 모델 온디바이스 경량화
* [ ] 금융기관 API 연동(지급정지 자동 안내)
* [ ] KISA 자동 신고 기능
* [ ] 지속학습(Fine-tuning) 자동화

---

<br>
# 🙏🏻 Git 협업 전략
**Commit Convention**

| Commit Type | Description |
| --- | --- |
| Feat | 기능 개발 |
| Fix | 버그 수정 |
| Docs | 문서 수정 |
| Refactor | 코드 리팩토링 |
| Design | CSS 등 사용자 UI 변경 |
| Test | 로직 및 코드 테스트 |

**PR Convention**

| Icon | 사용법 | Description |
| --- | --- | --- |
| 🎨 Design | `:art` | UI/스타일 파일 추가/수정 |
| ✨ Feature | `:sparkles` | 새로운 기능 도입 |
| 🔥 Fix | `:fire` | 버그 수정 |
| ✅ Test | `:white_check_mark`   | 로직 및 코드 테스트 |
| ♻️ Refactoring | `:recycle` | 코드 리팩토링 |
| 📘 Docs | `:blue_book` | Feature 이외에 문서 생성 및 수정 |

**협업 전략 !필독!**

**Git-flow 전략**

- Git-flow 전략을 기반으로 main, develop 브랜치와 feature 보조 브랜치를 운용.
- main, develop, Feat 브랜치로 나누어 개발을 하였습니다. - **main** 브랜치는 무결성 검증 이후 단계에서만 사용하는 브랜치입니다. - **develop** 브랜치는 개발 단계에서 git-flow의 master 역할을 하는 브랜치입니다. - **Feat** 브랜치는 기능 단위로 독립적인 개발 환경을 위하여 사용하고 merge 후 각 브랜치를 삭제해주었습니다.

**GitHub Role**

- 사용자는 먼저 Upstream Repository를 자신의 GitHub 계정으로 포크(fork)하고, 이 포크(fork)된 Origin Repository를 로컬 컴퓨터로 **Clone**하여 작업합니다.
- 그 후 개발한 변경 사항을 Origin Repository로 **Push**합니다. 이후 Upstream Repository로 풀 **PR**를 보내 변경 사항을 제안합니다.
- PR이 완료 된 후 Upstream Repository의 최신 변경 사항을 가져오기 위해 Local에서 풀(pull)을 사용합니다.

---

**개발을 시작할 때**

1. 개발을 시작할 때는 Upstream Repository에서 Issue를 생성합니다.
2. 이후 Issue에서 Origin Repository의 Dev Branch에서 새로운 Branch를 생성합니다
    - 이때 브랜치 이름은 다음을 따릅니다.
    - **새로운 기능 개발 : feature/#[Issue의 번호]**
    - **버그 픽스 : fix/#[Issue의 번호]**
    - **기능 리팩토링 : refactor/#[Issue의 번호]**
3. Loacl에서 Fetch를 통해 만든 New Branch(feature or fix or refactor)을 들고옵니다.
4. 해당 Branch로 checkout 이후 기능 개발을 진행합니다.

**개발을 종료할 때**

1. 기능 개발이 종료되면 Origin Repository의 Branch(feature or fix or refactor)로 변경 사항을 Push 합니다.
2. Origin Repository에서 Upstream Repository로 PR을 보냅니다.
3. Code Review 이후 마지막으로 Approve한 사람은 **Squash And Merge**를 합니다. `리뷰생략가능`
4. PR이 **Squash And Merge**되면 Local에서는 dev Branch로 checkout합니다.
5. Local에서 Upstream Repository의 dev Branch를 pull 받습니다.
6. 마지막으로 Origin Repository의 dev Branch를 Update하기 위해 Push를 해줍니다.

**Main Branch가 갱신될 때**

1. 만약 Release Version을 낼 때는 Upstream의 dev Branch에서 main Branch로 PR을 날립니다.
2. 해당 Repository의 모든 사용자가 Code를 재확인한 후 Merge를 합니다.
