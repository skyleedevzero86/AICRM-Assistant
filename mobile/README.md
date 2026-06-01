# AICRM Mobile

고객용 모바일 앱입니다. Expo 기반 React Native 앱으로, 상담원/관리자용 Next.js 웹과 분리되어 같은 백엔드 API를 호출합니다.

```text
mobile/   React Native + Expo 고객 앱
frontend/ Next.js 상담원/관리자 웹
backend/  Spring Boot 공통 API
ai-worker/ AI/RAG 내부 작업
```

## Run

```bash
npm install
npm run start
```

API 주소는 `app.json`의 `expo.extra.apiBaseUrl`에서 바꿀 수 있습니다.
