<img width="1221" height="668" alt="image" src="https://github.com/user-attachments/assets/3a4e0c63-a169-44f9-a402-f11c778cd424" />
<br/>

# AICRM-Assistant

AI 기반 콜센터 상담원 Copilot + CRM 자동화 + RAG/GraphRAG 상담 지식 시스템 프로젝트입니다.

## 프로젝트 목표

- 상담 티켓, 고객, 메시지, 첨부파일을 관리하는 콜센터 업무 시스템 구축
- 상담원이 바로 활용할 수 있는 AI 답변 초안, 상담 요약, 유사 상담 추천 제공
- FAQ, 정책 문서, 과거 상담 이력을 기반으로 한 RAG 검색 지원
- 고객-문의-상품-정책-해결책 관계를 활용하는 GraphRAG 확장 기반 마련

## 아키텍처 구조

```text
[Next.js Frontend]
  - 상담원 화면
  - 관리자 대시보드
  - 고객/티켓/문서 관리 UI
          |
          | REST / SSE
          v
[Spring Backend]
  - core-api-service
    * Spring MVC
    * JPA / Flyway / Security
    * 고객, 티켓, 메시지, CRM, 관리자 기능
  - chat-ai-service
    * Spring WebFlux
    * SSE 스트리밍
    * Spring AI 기반 답변 초안 생성
          |
          | 비즈니스 데이터 조회 / AI 요청
          v
[Data & Infra]
  - PostgreSQL + pgvector
    * 고객, 티켓, 메시지, AI 실행 이력
    * 문서 chunk / embedding 저장
  - Redis
    * 캐시, 세션, 스트리밍 보조
  - MinIO
    * 문서 원본, 첨부파일 저장
  - Neo4j
    * GraphRAG 관계 그래프 저장
          |
          | 문서 파싱 / 임베딩 / 그래프 추출
          v
[Python AI Worker]
  - FastAPI
  - 문서 파싱
  - RAG 인덱싱
  - GraphRAG 실험
  - LangGraph 기반 멀티 스텝 워크플로우 확장
```

## 서비스 구성

<br/><br/>
<img width="1035" height="686" alt="image" src="https://github.com/user-attachments/assets/ab22ac1d-6128-4c52-b1be-20ba16ce6e3c" />
<br/><br/>

### 1. Frontend

- 경로: `frontend`
- 기술: Next.js, TypeScript, Tailwind CSS
- 역할:
  - 상담원용 티켓/대화 화면
  - AI 추천 패널
  - 관리자 대시보드

### 2. Core API Service

- 경로: `backend/core-api-service`
- 기술: Spring Boot, Spring MVC, JPA, Flyway, Spring Security
- 역할:
  - 고객, 상담원, 티켓, 메시지 CRUD
  - 상담 이력 관리
  - CRM 액션 및 감사 로그 관리

### 3. Chat AI Service

- 경로: `backend/chat-ai-service`
- 기술: Spring Boot, Spring WebFlux, SSE, Spring AI
- 역할:
  - 상담 답변 초안 스트리밍
  - AI 요약 및 추천 응답 생성
  - 향후 Tool Calling / MCP 연동 확장

### 4. AI Worker

- 경로: `ai-worker`
- 기술: FastAPI, LangGraph, Neo4j GraphRAG, pgvector
- 역할:
  - 문서 업로드 후 텍스트 추출
  - chunk 분할 및 embedding 생성
  - PostgreSQL / Neo4j 색인
  - GraphRAG 실험 워크플로우 처리

## 데이터 저장소

- PostgreSQL + pgvector
  - 업무 데이터와 벡터 검색을 한 DB에서 함께 관리
- Redis
  - 캐시, 세션, 스트림성 데이터 처리
- MinIO
  - PDF, 첨부파일, 문서 원본 저장
- Neo4j
  - 고객-문의-정책-해결책 관계 그래프 저장

## 개발 단계

1. 콜센터 업무 시스템
   티켓, 고객, 메시지, 파일 업로드 중심의 MVP 구축
2. 일반 RAG 상담 도우미
   문서 업로드, chunking, embedding, 유사 문서 검색, 답변 초안 생성
3. CRM 자동화
   상담 요약, 감정 분석, 카테고리 분류, 우선순위 추천
4. GraphRAG 확장
   Neo4j 기반 관계 추론, 이슈-정책-해결책 연결 분석
5. Multi-Agent / MCP 확장
   Intent, Retriever, CRM, Policy Checker Agent로 확장

## 실행 방법

### 1. 인프라 실행

```bash
docker compose up -d
```

### 2. Backend 실행

인프라(PostgreSQL, Redis)를 먼저 실행합니다.

```bash
docker compose up -d postgres redis
```

| Service    | Host Port | Credentials                                   |
| ---------- | --------- | --------------------------------------------- |
| PostgreSQL | 5433      | db: `aicrm`, user: `aicrm`, password: `aicrm` |
| Redis      | 9379      | password: `123456`                            |

로컬 설정 파일 생성:

```bash
cp backend/core-api-service/src/main/resources/application-local.yml.example backend/core-api-service/src/main/resources/application-local.yml
cp backend/chat-ai-service/src/main/resources/application-local.yml.example backend/chat-ai-service/src/main/resources/application-local.yml
```

`application-local.yml` 파일에 DB·Redis·OpenAI API 키를 입력합니다. 해당 파일은 git에 커밋되지 않습니다.

백엔드 실행:

```bash
./gradlew :backend:app:bootRun
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

### API 문서 (Swagger)

백엔드 실행 후 아래 URL에서 API 문서를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

- **Authorize** 버튼에서 `Bearer {JWT}` 형식으로 토큰 입력
- 로그인(`POST /api/auth/login`) 응답의 `accessToken` 사용
- 인증 / 고객 / 상담원 / 첨부파일 API 문서 제공

chat-ai-service는 OPENAI_API_KEY, OPENAI_CHAT_MODEL 환경 변수를 사용합니다.

기본 인증 API:

- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/signup/customer`
- `POST /api/auth/signup/agent`
- `POST /api/admin/agents/{agentId}/approve`
- `POST /api/auth/withdraw`
- `GET /api/admin/users/customers`
- `GET /api/admin/users/agents`
- `POST /api/admin/users/{userId}/suspension`
- `POST /api/admin/users/{userId}/withdrawal`
- `GET /api/admin/attendance/agents`

관리자 페이지:

- `frontend/app/admin/users/customers/page.tsx`
- `frontend/app/admin/users/agents/page.tsx`
- `frontend/app/admin/attendance/page.tsx`

테스트 계정:

- 관리자: `admin@aicrm.local` / `password`
- 상담원(활성): `agent1@aicrm.local` / `password`
- 상담원(대기): `agent-pending@aicrm.local` / `password`
- 고객: `customer@example.com` / `password`

### 3. Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

### 4. Mobile App

고객용 모바일 앱은 `mobile` 디렉터리에 있으며 Expo 기반 React Native 앱입니다.

```bash
cd mobile
npm install
npm run start
```

Expo Go 앱으로 QR 코드를 스캔하여 실행할 수 있습니다.

API 주소는 `mobile/app.json`의 `expo.extra.apiBaseUrl`에서 변경할 수 있습니다. 실기기(Expo Go)에서는 `localhost` 대신 PC의 LAN IP(예: `http://192.168.0.10:8080`)를 사용해야 하며, 백엔드가 먼저 실행되어 있어야 합니다.

자세한 내용은 [mobile/README.md](mobile/README.md)를 참고하세요.

### 5. AI Worker 실행

```bash
cd ai-worker
python -m venv .venv
.venv/Scripts/activate
pip install -e ".[dev]"
uvicorn aicrm_worker.main:app --reload
```
