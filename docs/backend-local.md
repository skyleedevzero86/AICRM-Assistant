# Backend Local Setup

## Prerequisites

- Java 21
- Docker

## Infrastructure

```bash
docker compose up -d postgres redis
```

| Service | Host Port | Credentials |
|---------|-----------|-------------|
| PostgreSQL | 5433 | db: `aicrm`, user: `aicrm`, password: `aicrm` |
| Redis | 9379 | password: `123456` |

## Run

```bash
./gradlew :backend:app:bootRun
```

Default profile: `local`

Create local config files before first run:

```bash
cp backend/core-api-service/src/main/resources/application-local.yml.example backend/core-api-service/src/main/resources/application-local.yml
cp backend/chat-ai-service/src/main/resources/application-local.yml.example backend/chat-ai-service/src/main/resources/application-local.yml
```

Edit `application-local.yml` files with your credentials. These files are gitignored.

| File | Contents |
|------|----------|
| `core-api-service/.../application-local.yml` | PostgreSQL, Redis, MinIO |
| `chat-ai-service/.../application-local.yml` | OpenAI API key |

## Verify

```bash
curl http://localhost:8080/actuator/health
```

Expected: HTTP 200 with `"status":"UP"` and database health included.

## API 문서 (Swagger)

백엔드 실행 후 브라우저에서 Swagger UI에 접속할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

JWT 인증이 필요한 API는 Swagger UI **Authorize** 버튼에서 `Bearer {accessToken}` 형식으로 토큰을 입력하세요. 로그인 API(`POST /api/auth/login`) 응답의 `accessToken` 값을 사용합니다.

Flyway runs migrations from `backend/core-api-service/src/main/resources/db/migration` on startup.

## 테스트

API 통합 테스트 및 실행 방법은 [backend-testing.md](./backend-testing.md)를 참고하세요.

## Module Layout

```text
backend/
├── app/
├── core-api-service/
│   └── src/main/java/com/aicrm/core/
│       ├── global/
│       ├── auth/
│       ├── customer/
│       ├── agent/
│       ├── ticket/
│       ├── conversation/
│       ├── message/
│       └── category/
└── chat-ai-service/
```

## API Response Format

Success:

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

Failure:

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "Invalid request"
  }
}
```
