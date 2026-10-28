# 수강신청 맞교환 데모

- `src/components/menu/ExchangeMenu.vue`: 강의 검색, 강의 상세/대기 현황, 내 페이지, 내 강의 업로드, 매칭 세션(조건 재검사 → 최종 확정 → 교환 완료)을 구현합니다.
- 데이터는 브라우저에서만 동작하는 데모 데이터이며 서버에 저장되지 않습니다.
- 교환 완료 시 데모 이벤트를 통해 기존 `수강신청내역` 목록에 받은 강의가 추가됩니다.
- 실제 API 연결 위치와 예시 경로는 `ExchangeMenu.vue`의 `// 실제 백엔드 연동` 주석을 참고하세요.
- 기존 `package-lock.json`에는 만료된 Taobao 레지스트리 URL이 있어 삭제했습니다. Codespaces에서 `npm install`을 실행하면 `package.json` 기준으로 새 잠금 파일이 생성됩니다.

## 실행

```bash
npm install
npm run serve
```

Codespaces의 **Ports** 탭에서 포트 8080을 열면 브라우저로 확인할 수 있습니다.
