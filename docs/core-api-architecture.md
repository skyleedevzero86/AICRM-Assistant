# Core API Architecture Guide

AICRM-Assistant `core-api-service` applies practical domain-centered design for MVP.

## Scope

Domains: `auth`, `attendance`, `customer`, `agent`, `ticket`, `conversation`, `message`, `category`

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
