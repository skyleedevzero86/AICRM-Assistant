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
| `core-api-service/.../application-local.yml` | PostgreSQL, Redis |
| `chat-ai-service/.../application-local.yml` | OpenAI API key |

## Verify

```bash
curl http://localhost:8080/actuator/health
```

Expected: HTTP 200 with `"status":"UP"` and database health included.

Flyway runs migrations from `backend/core-api-service/src/main/resources/db/migration` on startup.

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
