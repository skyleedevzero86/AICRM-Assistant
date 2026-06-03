# Core API Architecture Guide

AICRM-Assistant `core-api-service` applies practical domain-centered design for MVP.

## Scope

Domains: `auth`, `attendance`, `customer`, `agent`, `ticket`, `conversation`, `message`, `category`, `attachment`

MVP use cases:

- Customer inquiry registration
- Ticket creation
- Agent ticket accept
- Message save
- Ticket close
- Consultation category tree query
- JWT login and current-user auth
- Customer/agent sign-up flow split
- Admin customer/agent user management
- Agent daily attendance tracking
- Ticket attachment upload and download URL

## Not in MVP

Axon, CQRS, Event Sourcing, CommandBus, EventBus, full AggregateRoot/DomainEvent, Kafka, heavy hexagonal adapters.

## Layer responsibilities

```text
controller  → HTTP request/response
application → use case orchestration, transaction boundary
domain      → business rules, state change methods
infrastructure → JPA/Spring Data implementation
dto         → request/response only
```

Dependency direction:

```text
controller → application → domain
application → repository interface (domain)
infrastructure → repository implementation
```

## Package layout

```text
com.aicrm.core/
├── global/
│   ├── config/
│   ├── exception/
│   └── response/
├── customer/
│   ├── controller/
│   ├── application/
│   ├── domain/
│   ├── infrastructure/
│   └── dto/
├── ticket/
├── conversation/
├── message/
├── attachment/
└── category/
```

Each domain package follows the same layer structure.

## Rules

1. Controllers stay thin: validate request, call application service, return `ApiResponse`.
2. Application services are use-case sized (`CreateCustomerInquiryService`, `GetConsultationCategoryTreeService`).
3. Entities do not use `@Setter`. Use factory methods and domain methods.
4. State changes include validation (`ticket.accept(agentId)`, `category.ensureLeafCategory()`).
5. DTOs are separate from entities.
6. Repository interface lives in `domain`, JPA adapter in `infrastructure`.
7. Business errors use `BusinessException` and `ErrorCode`.

## Repository pattern

```text
domain/TicketRepository.java              (interface)
infrastructure/JpaTicketRepository.java   (implements interface)
infrastructure/TicketSpringDataJpaRepository.java (Spring Data)
```

Application layer depends on `TicketRepository`, not Spring Data types.

## Completion checklist

- Domain packages with controller/application/domain/infrastructure/dto
- No repository calls from controllers
- No business logic in controllers
- Use-case application services
- No entity setters
- Domain methods for state change
- DTO/entity separation
- Repository interface and JPA implementation separation
- Shared exception handling
- No Axon/CQRS/Event Sourcing in MVP

## API 문서 (Swagger)

Core API는 springdoc-openapi 기반 Swagger UI를 제공합니다.

| 항목 | 경로 |
|------|------|
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |

### JWT 인증

1. `POST /api/auth/login`으로 로그인하여 `accessToken`을 발급받습니다.
2. Swagger UI 상단 **Authorize** 버튼을 클릭합니다.
3. `Bearer {accessToken}` 형식으로 토큰을 입력합니다.
4. 인증이 필요한 API(고객/상담원/첨부파일)를 호출합니다.

### API 태그

| 태그 | 설명 |
|------|------|
| 인증 | 로그인, 회원가입, 내 정보 |
| 고객 | 문의 등록, 티켓 조회/수정, 메시지 |
| 상담원 | 대기 티켓, 접수, 종료, 메시지 |
| 첨부파일 | 업로드, 목록 조회, 다운로드 URL |

설정 클래스: `global/config/OpenApiConfig.java`

## 테스트

백엔드 테스트 실행 방법과 MVP API 검증 범위는 [backend-testing.md](./backend-testing.md)를 참고합니다.

- 단위·MockMvc: `backend/core-api-service/src/test/java`
- API 통합(Testcontainers): `backend/app/src/test/java`
- 오류 메시지: `shared/messages/ko.json`과 동일한 한국어 문구 검증
