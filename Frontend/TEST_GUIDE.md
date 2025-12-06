# SMS 테스트 가이드 (에뮬레이터)

## 방법 1: 에뮬레이터 Extended Controls 사용 (가장 쉬움)

1. **에뮬레이터 실행**
2. **오른쪽 메뉴에서 "..." (More) 클릭**
3. **"Phone" 탭 선택**
4. **"Send Message" 클릭**
5. **메시지 입력 후 전송**
   - Phone number: 아무 번호나 입력 (예: 01012345678)
   - Message: 테스트할 메시지 입력
6. **앱에서 SMS 수신 확인**

## 방법 2: ADB 명령어 사용

### 간단한 방법 (권장)
터미널에서 다음 명령어 실행:

```bash
# 위험 메시지 테스트
adb shell am broadcast -a android.provider.Telephony.SMS_RECEIVED --es sender "01012345678" --es message "안녕하세요! Casino 이벤트 참여시 100% 보장! 지금 https://casino.xyz 클릭하세요!"

# 경고 메시지 테스트
adb shell am broadcast -a android.provider.Telephony.SMS_RECEIVED --es sender "15881234" --es message "KB국민카드입니다. 계좌 확인을 위해 링크를 클릭해주세요. https://kb-card.com"

# 정상 메시지 테스트
adb shell am broadcast -a android.provider.Telephony.SMS_RECEIVED --es sender "15881234" --es message "[KB국민카드] 데이터 분석 부트캠프 안내. 최대 170만원 지원금과 인턴십 기회까지 제공합니다."
```

**주의**: 이 방법은 BroadcastReceiver가 Intent의 extra를 직접 읽을 수 있도록 수정이 필요합니다.

## 방법 3: 앱 내 테스트 버튼 사용 (현재 구현됨)

1. **앱 실행**
2. **홈 화면 상단 오른쪽 "테스트" 버튼 클릭**
3. **원하는 테스트 선택**:
   - 위험 메시지
   - 경고 메시지
   - 정상 메시지

이 방법은 실제 SMS 수신과 동일한 흐름으로 동작합니다.

## 방법 4: 실제 기기에서 다른 기기로 SMS 전송

실제 기기가 있다면:
1. 다른 기기에서 테스트 메시지 전송
2. 앱이 자동으로 감지하여 분석

## 권장 테스트 순서

1. **먼저 방법 3 (앱 내 테스트 버튼)으로 기본 동작 확인**
2. **방법 1 (에뮬레이터 Extended Controls)로 실제 SMS 수신 흐름 테스트**
3. **방법 2 (ADB)로 자동화된 테스트**

