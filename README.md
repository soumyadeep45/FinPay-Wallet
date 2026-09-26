
# FinPay Digital Wallet API 🏦

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)

A highly concurrent, secure, and production-ready digital wallet microservice. FinPay handles user authentication, digital wallet management, secure peer-to-peer money transfers, and asynchronous webhook notifications. 

Built with a focus on enterprise-grade data integrity, this API demonstrates advanced backend architecture patterns including pessimistic row-level locking, idempotency guarantees, and decoupled testing environments.

---

## 🚀 Architectural Highlights

* **Concurrency Control (ACID):** Utilizes PostgreSQL row-level pessimistic locking (`SELECT ... FOR UPDATE`) during transfers to mathematically eliminate race conditions and double-spending across concurrent threads.
* **Idempotency:** Implements unique idempotency keys on payment endpoints to safely handle network retries and prevent duplicate transaction processing.
* **Stateless Security:** Secured via JWT (JSON Web Tokens) with strictly enforced Role-Based Access Control (RBAC) separating `USER` and `ADMIN` privileges.
* **Event-Driven Webhooks:** Utilizes Spring's modern `RestClient` and `@Async` processing to fire non-blocking HTTP callback notifications to external systems upon successful transfers.
* **Data Hygiene & DTOs:** Employs Jakarta Validation and strictly mapped Data Transfer Objects (DTOs) to sanitize incoming requests and prevent sensitive data leaks (e.g., hidden BCrypt password hashes).
* **CI/CD Ready Testing:** Decouples the testing environment by utilizing an ephemeral H2 In-Memory database, guaranteeing zero-setup, consistently green integration tests for automated pipelines.

---

## 🛠️ Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot (Web, Data JPA, Security, Validation)
* **Database:** PostgreSQL (Production) / H2 In-Memory (Testing)
* **Authentication:** JSON Web Tokens (JWT) & BCrypt
* **Containerization:** Docker & Docker Compose
* **Documentation:** OpenAPI / Swagger UI

---

## 📦 Local Setup & Deployment

### Prerequisites
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.
* Java 21+ and Maven (if running locally outside of Docker).

### 1. The Full Docker Workflow (Recommended)
This command builds the Spring Boot `.jar` and spins up both the application server and the PostgreSQL database in isolated containers.

```bash
# Clone the repository
git clone [https://github.com/yourusername/finpay-wallet.git](https://github.com/yourusername/finpay-wallet.git)
cd finpay-wallet

# Build and start the infrastructure in the background
docker compose up --build -d

# View live application logs
docker compose logs -f

```

The API will be live at `http://localhost:8080`.

### 2. The Development Workflow (IDE + Docker DB)

If you want to run the Java code in your IDE (IntelliJ/VS Code) for debugging while keeping the database in Docker:

```bash
# Start ONLY the PostgreSQL database on exposed port 5433
docker compose up -d db

```

Then, run the `WalletServiceApplication` from your IDE.

---

## 🧪 Testing

The test suite is fully decoupled from PostgreSQL and utilizes a temporary H2 in-memory database, ensuring tests can be run instantly on any machine or CI/CD pipeline without database configuration.

```bash
# Run the integration and unit test suite
./mvnw clean test

```

---

## 📖 API Documentation (Swagger)

Once the application is running, the interactive OpenAPI documentation is automatically generated. You can test endpoints directly from your browser:

* **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
* **API Docs (JSON):** `http://localhost:8080/v3/api-docs`

### Core Endpoints Overview

| HTTP Method | Endpoint | Description | Role Required |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Register a new user and auto-provision a wallet | Public |
| `POST` | `/api/auth/login` | Authenticate and receive a JWT | Public |
| `GET` | `/api/wallets/balance` | Securely fetch the authenticated user's balance | `USER` |
| `POST` | `/api/wallets/deposit` | Add funds to the authenticated wallet | `USER` |
| `POST` | `/api/wallets/transfer` | Securely transfer funds (requires `Idempotency-Key` header) | `USER` |
| `GET` | `/api/wallets/transactions` | Fetch paginated transaction history | `USER` |
| `POST` | `/api/webhooks` | Register an external URL for payment notifications | `USER` |
| `GET` | `/api/admin/users` | Fetch paginated list of all users via safe DTOs | `ADMIN` |
| `PATCH` | `/api/admin/users/{id}/suspend` | Instantly revoke user access and freeze wallet | `ADMIN` |

---

## 👨‍💻 Author

**Soumyadeep Patra**

* B.Tech in Computer Science and Information Technology (Class of 2027)
* Focused on Backend Software Engineering, REST API Design, and Microservice Architecture.

```

```
