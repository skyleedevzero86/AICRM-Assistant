# 백엔드 테스트 가이드

`core-api-service`와 `app` 모듈의 테스트는 [core-api-architecture.md](./core-api-architecture.md)의 MVP API 흐름과 `shared/messages/ko.json` 한국어 오류 메시지 규칙을 기준으로 작성되어 있습니다.

## 사전 요구 사항

- Java 21
- Docker 및 PostgreSQL (통합 테스트 DB)

통합 테스트 실행 전 인프라를 기동합니다.

```bash
docker compose up -d postgres
```

통합 테스트는 `localhost:5433`의 `aicrm` 데이터베이스(`application-test.yml`)에 Flyway 마이그레이션을 적용한 뒤 실행합니다.

## 테스트 실행

### 전체 백엔드 테스트

```bash
./gradlew :backend:core-api-service:test :backend:app:test
```

### 모듈별 실행

```bash
./gradlew :backend:core-api-service:test
./gradlew :backend:app:test
```

### 단일 테스트 클래스

```bash
./gradlew :backend:app:test --tests "com.aicrm.app.AuthApiIntegrationTest"
```

## 테스트 구성

| 구분 | 위치 | 설명 |
|------|------|------|
| 단위 테스트 | `backend/core-api-service/src/test/java` | 애플리케이션 서비스·도메인·MockMvc 컨트롤러 |
| API 통합 테스트 | `backend/app/src/test/java` | `@SpringBootTest` + 로컬 PostgreSQL MockMvc |
| 공통 지원 | `backend/app/src/test/java/com/aicrm/app/support` | 로그인·JWT·카테고리 조회 헬퍼 |

## 검증 범위 (MVP v0.2.0)

### 인증 API (`AuthApiIntegrationTest`)

- 고객·상담원 로그인 및 JWT 발급
- 잘못된 비밀번호 시 `AUTH_FAILED` 한국어 메시지
- 보호 API JWT 필수 (`UNAUTHORIZED` 한국어 메시지)
- 역할 기반 접근 제어 (`FORBIDDEN` 한국어 메시지)
- `/api/auth/me` 현재 사용자 조회

### 고객 API

- `ConsultationCategoryApiIntegrationTest`: 상담 구분 트리 조회, 3단계 리프 문의 등록, 1단계 카테고리 거부
- `CustomerTicketApiIntegrationTest`: 본인 티켓만 조회, 타 고객 티켓 `TICKET_NOT_FOUND`
- `CustomerInquiryControllerTest`: 문의 등록 MockMvc

### 상담원 API

- `AgentTicketApiIntegrationTest`: 대기 목록, 접수, 중복 접수 거부, 비배정 상담원 종료 거부, 종료
- `AgentTicketControllerTest`: 접수·종료 MockMvc 및 권한 오류
- `TicketMessageApiIntegrationTest`: 고객·상담원 메시지, 미배정 상담원 메시지 거부

### 첨부파일 API

- `AttachmentApiIntegrationTest`: 고객·상담원 업로드, 목록, 다운로드 URL, 확장자 거부
- `CustomerTicketAttachmentControllerTest`, `AgentTicketAttachmentControllerTest`: MockMvc

### MVP 핵심 흐름

- `MvpCoreFlowIntegrationTest`: 로그인 → 문의 → 첨부 → 접수 → 메시지 → 종료 → 조회

## 시드 계정 (통합 테스트)

| 역할 | 이메일 | 비밀번호 |
|------|--------|----------|
| 고객 | `customer@example.com` | `password` |
| 상담원1 | `agent1@aicrm.local` | `password` |
| 상담원2 | `agent2@aicrm.local` | `password` |

Flyway `V14__ensure_seed_accounts_active.sql`에서 통합 테스트용 계정 상태(탈퇴/정지 해제, 비밀번호)를 보장합니다.

## 테스트 작성 규칙

1. 프로덕션 코드에 설명 주석을 추가하지 않습니다.
2. API 오류 응답은 `shared/messages/ko.json`의 한국어 `message`를 검증합니다.
3. 테스트 메서드 내부에는 `// given`, `// when`, `// then`(또는 `// when & then`) 주석만 사용합니다.
4. 통합 테스트 클래스는 `PostgresIntegrationTestSupport`를 상속하고 `@ActiveProfiles("test")`를 사용합니다.

## Testcontainers 검토

현재 통합 테스트는 `backend-local.md`와 동일한 로컬 PostgreSQL(5433)을 사용합니다. CI에서 Docker만 제공되고 고정 포트 DB가 없을 때는 `org.testcontainers:postgresql` 의존성과 `@Testcontainers` 기반 `DynamicPropertySource`로 전환할 수 있습니다. 로컬 개발 흐름과의 일치를 위해 MVP 단계에서는 로컬 DB 방식을 채택했습니다.

## 완료 체크리스트

- [x] Auth API 테스트
- [x] Customer API 테스트
- [x] Agent API 테스트
- [x] File API 테스트
- [x] 권한 실패 케이스 테스트
- [x] 핵심 MVP 흐름 테스트
- [x] 테스트 실행 방법 README (`docs/backend-testing.md`)
