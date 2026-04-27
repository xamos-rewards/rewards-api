# Rewards System

A centralized platform for managing and tracking employee reward points across multiple internal and external applications. Built with high-concurrency and modern security as core principles.

## Technology Stack

- **Backend**: Java 25, Spring Boot 4.0.5, Spring Data JPA, Hibernate.
- **Concurrency**: Fully optimized for Virtual Threads (Project Loom).
- **Identity & Security**: Auth0 (OAuth 2.0 / OIDC), Stateless JWT Resource Server.
- **Database**: PostgreSQL (Production/Dev), H2 (Testing).
- **Frontend**: Angular 19, TypeScript, RxJS.

## Security Model: Composite Identity Authentication

The system implements a robust Composite Identity model carried within standard Auth0 JWTs. Authorization is determined by combining the identity of the user and the application:

1.  **Identity Context**: Injected via Auth0 Actions, identifying the caller as a `user` or `application`.
2.  **User Identity (sub)**: The human actor initiating the request.
3.  **Application Identity (azp)**: The registered application facilitating the request.

The system enforces **Graceful Capability Reduction**:
- Requests with a valid User + Active Application context have full mutation capabilities.
- Requests with User context but no `azp` (direct login) are restricted to self-management.
- Application-only (M2M) requests are restricted to administrative and metadata tasks.

All reward mutations utilize **Implicit Self-Targeting**, where the target account is derived solely from the `sub` claim of the verified principal.

## Auth0 Management Integration

The system leverages the **Auth0 SDK v3.4.0** for dynamic application lifecycle management:
- **Automated Client Registration**: Creates Auth0 Clients and manages metadata.
- **Virtual Thread Optimized**: Utilizes an OkHttp-based networking stack with custom Virtual Thread dispatchers for high-throughput management tasks.

## Getting Started

### Prerequisites
- Java 25 JDK
- Docker (for local PostgreSQL via Compose)
- Auth0 Tenant with `identity_context` claims configured in Actions

### Running the API
```bash
cd rewards-api
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
The API will be available at http://localhost:10000.

## Testing

The project uses a multi-phase testing strategy:

- **Unit Tests**: Isolated logic tests (Surefire Plugin).
  ```bash
  ./mvnw test
  ```
- **Integration Tests**: Full-stack tests with H2 database (Failsafe Plugin).
  ```bash
  ./mvnw failsafe:integration-test
  ```
- **Full Suite**:
  ```bash
  ./mvnw verify
  ```
