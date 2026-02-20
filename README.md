# NeighborCart (Angular + Spring Boot + PostgreSQL)

NeighborCart is a neighborhood order-sharing app where nearby people collaborate to hit minimum cart values and avoid delivery fees.

## Stack

- Frontend: Angular 19 (standalone components, reactive forms, HTTP interceptor)
- Backend: Spring Boot 3.4 (Web, Security, JPA)
- Database: PostgreSQL 16
- Migrations: Flyway
- Containerization: Docker + Docker Compose

## Implemented features

- Interactive feed for nearby order-share posts with radius + store filters
- Create post flow with delivery ETA, cart threshold, masked contact, notes
- Post-level chat between users
- Interest workflow (`I'm in`) with duplicate protection
- Secure OTP login flow:
  - request OTP
  - verify OTP
  - token-based authenticated session
- Secure contact reveal behavior:
  - only post owner can toggle reveal state
  - real phone visible only to owner or interested users when reveal is enabled
- Ad placement slots in hero and feed UI
- PostgreSQL persistence for users, posts, interests, chats, OTP, sessions

## Project structure

- `/backend`: Spring Boot API + Flyway migrations
- `/frontend`: Angular app + nginx production config
- `/docker-compose.yml`: Full stack orchestration (frontend + backend + postgres)

## API overview

Base path: `/api`

Auth:

- `POST /auth/request-otp`
- `POST /auth/verify-otp`
- `GET /auth/me`
- `POST /auth/logout`

Posts/Chat:

- `GET /health`
- `GET /posts?lat=&lng=&radiusMiles=&store=`
- `GET /posts/{postId}`
- `POST /posts` (auth)
- `POST /posts/{postId}/interest` (auth)
- `POST /posts/{postId}/reveal-contact` (auth, owner only)
- `GET /posts/{postId}/chat`
- `POST /posts/{postId}/chat` (auth)

## Local development

Prerequisites:

- Java 21+
- Maven
- Node.js 20+
- PostgreSQL running locally

Create DB and user (example):

```sql
CREATE DATABASE ordershare;
CREATE USER ordershare WITH PASSWORD 'ordershare';
GRANT ALL PRIVILEGES ON DATABASE ordershare TO ordershare;
```

Start backend:

```bash
cd backend
mvn spring-boot:run
```

Start frontend:

```bash
cd frontend
npm install
npm start
```

Open app at [http://localhost:4200](http://localhost:4200).

Notes:

- Frontend dev server proxies `/api` to `http://localhost:8080` using `frontend/proxy.conf.json`.
- In local dev, OTP response may include `devOtpCode` if `APP_AUTH_DEV_OTP_ENABLED=true`.

## Docker deployment

1. Copy env template:

```bash
cp .env.example .env
```

2. Update secrets in `.env`.

3. Build and run:

```bash
docker compose up --build -d
```

4. Access app at [http://localhost](http://localhost).

Compose services:

- `postgres` on `5432`
- `backend` on `8080`
- `frontend` on `80` (nginx, proxies `/api` to backend)

## Environment variables

Backend:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_AUTH_DEV_OTP_ENABLED`
- `APP_CORS_ALLOWED_ORIGINS`

## Next production steps

1. Integrate real SMS provider (Twilio/MessageBird) for OTP delivery.
2. Add rate limiting and abuse controls for OTP and post/chat endpoints.
3. Add observability (metrics, logs, tracing) and centralized alerting.
4. Add background jobs for post expiration and stale-session cleanup.
5. Introduce payment escrow flow to avoid off-platform cash handoff risk.
