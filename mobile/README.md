# AICRM Mobile

고객용 모바일 앱입니다. Expo + React Native 기반이며, Spring Boot 백엔드 API와 연동합니다.

```text
mobile/   React Native + Expo 고객 앱
frontend/ Next.js 상담원/관리자 웹
backend/  Spring Boot 공통 API
```

## 사전 요구 사항

- Node.js 18+
- npm
- [Expo Go](https://expo.dev/go) (실기기 테스트)
- 실행 중인 AICRM 백엔드 (`http://localhost:8080`)

백엔드는 저장소 루트에서 다음으로 실행합니다.

```bash
docker compose up -d postgres redis
./gradlew :backend:app:bootRun
```

## 설치 및 실행

```bash
cd mobile
npm install
npm run start
```

터미널에 표시되는 QR 코드를 Expo Go 앱으로 스캔하면 Android/iOS에서 실행할 수 있습니다.

| 명령 | 설명 |
| --- | --- |
| `npm run start` | Expo 개발 서버 (QR 코드) |
| `npm run android` | Android 에뮬레이터/기기 |
| `npm run ios` | iOS 시뮬레이터 (macOS) |
| `npm run typecheck` | TypeScript 검사 |

## API 주소 설정

앱은 Expo LAN IP → Android 에뮬레이터(`10.0.2.2`) 순으로 API 주소를 자동 시도합니다.

자동 연결이 안 되면 `mobile/.env` 파일을 만들고 PC의 LAN IP를 지정합니다.

```bash
cp .env.example .env
```

```env
EXPO_PUBLIC_API_BASE_URL=http://192.168.0.10:8080
```

Expo Go 실기기 테스트 시 `localhost`는 사용할 수 없습니다. PC와 휴대폰이 같은 Wi-Fi에 있어야 하며, 백엔드는 `0.0.0.0:8080`으로 실행됩니다.

`npx expo start --tunnel` 사용 시 API는 LAN IP를 직접 지정해야 합니다.

변경 후 `npm run start`를 다시 실행하세요.

## 주요 화면

| 탭 | 경로 | 설명 |
| --- | --- | --- |
| 인증 시작 | `/` | 토큰 유무에 따라 로그인 또는 탭으로 이동 |
| 로그인 | `/auth/login` | 이메일/비밀번호 로그인 |
| 고객 회원가입 | `/auth/signup-customer` | 가입 즉시 활성화, 자동 로그인 |
| 상담원 회원가입 | `/auth/signup-agent` | 가입 후 승인 대기 |
| 홈 | `/(tabs)/` | 최근 접수 문의 |
| 문의 | `/(tabs)/inquiry` | 상담 구분 선택 후 문의 접수 |
| 내 문의 | `/(tabs)/tickets` | AsyncStorage + API 동기화 목록 |
| 상세 | `/tickets/[ticketId]` | 티켓 상세·메시지 이력 |

접수한 티켓은 `@react-native-async-storage/async-storage`에 저장되며, 앱을 다시 실행해도 이 기기에서 조회할 수 있습니다.

## Expo Go 실행 확인 (v0.1.1)

| 항목 | 결과 |
| --- | --- |
| `npm run typecheck` | 통과 |
| Android 번들 빌드 (`npx expo export --platform android`) | 통과 |
| `npm run start` + QR 코드 | Expo CLI 기본 동작 (로컬에서 `npm run start` 실행) |
| Android Expo Go 실기기 | LAN IP로 `apiBaseUrl` 설정 후 확인 |
| iOS Expo Go | Windows 개발 환경에서는 미확인 (macOS/iPhone에서 동일 절차로 확인) |

Windows 개발 환경에서는 Android Expo Go 실행을 우선 확인하고, iOS는 macOS 또는 Expo Go for iOS가 있는 환경에서 확인합니다.

## 인증 API

| API | 설명 |
| --- | --- |
| `POST /api/auth/login` | JWT 발급 |
| `GET /api/auth/me` | 현재 로그인 사용자 조회 |
| `POST /api/auth/signup/customer` | 고객 회원가입 |
| `POST /api/auth/signup/agent` | 상담원 회원가입(승인 대기) |

## 테스트 계정

| 역할 | 이메일 | 비밀번호 |
| --- | --- | --- |
| 관리자 | `admin@aicrm.local` | `password` |
| 활성 상담원 | `agent1@aicrm.local` | `password` |
| 승인 대기 상담원 | `agent-pending@aicrm.local` | `password` |
| 고객 | `customer@example.com` | `password` |
