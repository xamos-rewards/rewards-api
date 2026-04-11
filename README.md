# Rewards System

A centralized platform for managing and tracking employee reward points across multiple internal and external applications. Built with high-concurrency and modern security as core principles.

## Technology Stack

- **Backend**: Java 25, Spring Boot 4.0.5, Spring Data JPA, Hibernate.
- **Concurrency**: Fully optimized for Virtual Threads (Project Loom).
- **Identity & Security**: Auth0 (OAuth 2.0 / OIDC), Stateless JWT Resource Server.
- **Database**: PostgreSQL (Production/Dev), H2 (Testing).
- **Frontend**: Angular 19, TypeScript, RxJS.

## Security Model: Composite Identity Authentication

The system implements a robust Composite Identity model. Sensitive operations require two valid identities proven within a single JWT:

1.  **User Identity (sub)**: Validates the human actor performing the action.
2.  **Application Identity (azp)**: Validates that the request originates from a registered and trusted 3rd-party application.

The Rewards API extracts these claims and verifies them against a local metadata store. If an application is not registered locally, the request is rejected even if the JWT is cryptographically valid.

## Auth0 Management Integration

The system includes a self-service registration flow for developer teams:
- **Dynamic Client Registration**: Automated creation of Auth0 Clients via the Management SDK.
- **AOP Interception**: An Aspect-oriented interceptor transparently handles Management API token validity.

## Getting Started

### Prerequisites
- Java 25 JDK
- Docker (for local PostgreSQL via Compose)
- Auth0 Tenant and Management API credentials

### Running the API
```bash
cd rewards-api
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
The API will be available at http://localhost:10000.

### Configuration
The following environment variables are required for full functionality:
- AUTH0_ISSUER_URI: Your Auth0 domain issuer.
- AUTH0_AUDIENCE: The API identifier.
- AUTH0_MANAGEMENT_CLIENT_ID: Client ID for the Management API.
- AUTH0_MANAGEMENT_CLIENT_SECRET: Client Secret for the Management API.
- AUTH0_MANAGEMENT_AUDIENCE: The Management API audience (usually https://[domain]/api/v2/).

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
