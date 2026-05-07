# Capstone Backend

Spring Boot microservice backend for the Locally capstone platform. The backend provides identity, profiles, listings, orders and reservations, payments, notifications, community posts, reviews, follows, verification, administration, and an API gateway for the React frontend.

## Tech Stack

- Java 21
- Spring Boot 3.4.2
- Spring Cloud 2024.0.0
- Gradle Kotlin DSL
- MongoDB
- Spring Cloud Gateway
- Spring Security and JWT
- OpenFeign/WebClient for service-to-service calls
- Cloudinary for listing and verification images
- Stripe for payments and seller onboarding
- Docker Compose for local multi-service runs

## Services

| Service | Port | Responsibility |
| --- | ---: | --- |
| `api-gateway` | 9000 | Single frontend entry point, CORS, and route forwarding |
| `profile-service` | 8081 | Personal/business profiles, settings, verification resubmission, profile deletion |
| `identity-service` | 8082 | Registration, login, JWT issuing, user records, roles, status |
| `verification-service` | 8083 | Business/role verification submissions and review support |
| `listing-service` | 8084 | Farm and restaurant surplus listings, image upload, inventory/status updates |
| `notifications-service` | 8085 | User notifications and read state |
| `order_reservation-service` | 8086 | Shopper orders, NGO reservations, pickup confirmation, payment orchestration |
| `admin-service` | 8087 | Admin dashboard, users, listings, profiles, orders, reservations, verifications, moderation, support |
| `community-service` | 8088 | Community posts, comments, reactions, visibility |
| `review-service` | 8089 | Product/seller reviews and rating averages |
| `stripe-payment-service` | 8090 | Stripe payment intents, refunds, fund release, seller Connect onboarding |
| `follow-service` | 8091 | Followers, following, blocking, and connection stats |

## API Gateway Routes

The frontend should call the gateway on `http://localhost:9000` in local development.

| Path | Target service |
| --- | --- |
| `/auth/**` | `identity-service` |
| `/profiles/**`, `/internal/profiles/**` | `profile-service` |
| `/api/listings/**` | `listing-service` |
| `/api/orders/**`, `/api/reservations/**` | `order_reservation-service` |
| `/api/verification/**` | `verification-service` |
| `/notifications/**`, `/ping` | `notifications-service` |
| `/admin/**`, `/support/**` | `admin-service` |
| `/api/community/**` | `community-service` |
| `/api/reviews/**` | `review-service` |
| `/api/payments/**`, `/api/sellers/**`, `/api/stripe/**` | `stripe-payment-service` |
| `/api/follows/**` | `follow-service` |

## Prerequisites

- Java 21
- Docker and Docker Compose
- MongoDB connection strings for each service database
- Cloudinary credentials
- Stripe API keys and webhook secret

## Running with Docker Compose

Build and start all services:

```bash
docker compose up --build
```

Start in the background:

```bash
docker compose up --build -d
```

Stop services:

```bash
docker compose down
```

The gateway will be available at:

```text
http://localhost:9000
```

## Running a Service Locally

Each service has its own Gradle wrapper. Example:

```bash
cd identity-service
./gradlew bootRun
```

On Windows PowerShell:

```powershell
cd identity-service
.\gradlew.bat bootRun
```

Local runs require the environment variables referenced by that service's `application.properties`, such as MongoDB URI, JWT secret, Cloudinary settings, Stripe settings, or dependent service URLs.

## Build and Test

Build all included modules from the repo root:

```bash
./gradlew build
```

Run tests for all modules:

```bash
./gradlew test
```

Run tests for one service:

```bash
cd listing-service
./gradlew test
```

## Repository Layout

```text
admin-service/
api-gateway/
community-service/
follow-service/
identity-service/
listing-service/
notifications-service/
order_reservation-service/
profile-service/
review-service/
stripe-payment-service/
verification-service/
docker-compose.yml
settings.gradle.kts
build.gradle.kts
```
