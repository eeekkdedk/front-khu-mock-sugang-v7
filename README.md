# 수강신청 맞교환 플랫폼 (통합 환경)

이 저장소는 프론트엔드(Vue)와 백엔드(Spring Boot)가 함께 구성된 Codespaces 통합 테스트 환경입니다.

## Codespaces에서 실행하는 방법

### 1. 백엔드 서버 실행
새 터미널을 열고 다음 명령어를 입력하세요.
```bash
cd backend/sugang_exchange_be-wip-day22
./gradlew bootRun
```
(8080 포트가 열립니다)

### 2. 프론트엔드 서버 실행
또 다른 터미널을 열고 다음 명령어를 입력하세요.
```bash
cd frontend/front-khu-mock-sugang-v4-main
npm run serve
```
(8081 포트가 열립니다)

### 3. 접속
포트 탭(Ports)에서 8081 포트의 'Open in Browser'(지구본 아이콘)를 클릭하여 접속하시면 프론트엔드와 백엔드가 연동된 상태로 테스트할 수 있습니다.
