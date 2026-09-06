# SkillMint Spring Boot Backend

Backend for the `dashboard.html` SkillMint UI using Spring Boot + PostgreSQL.

## Implemented modules
- Authentication (`/api/auth/register`, `/api/auth/login`) with JWT generation.
- Dashboard stats (`/api/dashboard/stats`) for coins, requests, messages, and skills.
- Skills API (`/api/skills`) for add/remove/view.
- Requests API (`/api/requests`) for send/accept/reject and sent/received lists.
- Messaging API (`/api/messages`) for send + conversation history.
- User discovery (`GET /api/users`) for explore page.
- Message contacts (`GET /api/messages/contacts`) for accepted partners.
- Wallet API (`/api/wallet/summary`) for balance and transactions.

## Architecture
- `domain.entity` + `domain.enums` for core models.
- `repository` for persistence.
- `service` + `service.impl` for business logic.
- `controller` for API layer.
- `dto` for request/response contracts.

## Run locally
1. Start PostgreSQL:
   - `docker compose up -d`
2. Run app:
   - `mvn spring-boot:run`

Default DB config:
- host: `localhost`
- port: `5432`
- db: `skillmint`
- user/pass: `postgres/postgres`

Override with env vars: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`.

## Notes for frontend integration
- Save JWT from `/api/auth/login` or `/api/auth/register`.
- Send header `Authorization: Bearer <token>` for all protected endpoints.
