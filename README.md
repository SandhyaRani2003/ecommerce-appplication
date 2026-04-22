# E-Commerce Application (Spring Boot)

This is a backend REST API for a basic e-commerce workflow: user auth, product browsing/creation, cart management, and order checkout.

I built this project to practice designing a clean Spring Boot service with JWT security, layered architecture, and unit tests around business logic.

## What this project currently does

- User registration and login using JWT
- Product APIs (list, fetch by id, create)
- Per-user cart management (add/update/remove items)
- Checkout flow to place an order from the current cart
- View order history for the logged-in user
- Swagger/OpenAPI docs for quick API exploration

## Tech stack

- Java 17
- Spring Boot 3.2.0
- Spring Web
- Spring Data JPA (Hibernate)
- Spring Security + JWT
- MySQL (runtime)
- JUnit 5 + Mockito (service-layer tests)
- Springdoc OpenAPI (Swagger UI)

## Project structure

`src/main/java/com/example/ecommerce`
- `controller` - REST endpoints
- `service` - business logic
- `repository` - JPA repositories
- `model` - entities
- `dto` - request/response contracts
- `security` - JWT and auth-related classes
- `config` - Spring Security and OpenAPI config

`src/test/java/com/example/ecommerce/service`
- Unit tests for service layer classes

## API overview

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`

### Products
- `GET /products`
- `GET /products/{id}`
- `POST /products`

### Cart (authenticated)
- `GET /api/cart`
- `POST /api/cart/items`
- `PUT /api/cart/items/{itemId}`
- `DELETE /api/cart/items/{itemId}`

### Orders (authenticated)
- `POST /api/orders/checkout`
- `GET /api/orders`

## Security behavior (important)

From current `SecurityConfig`:
- Public endpoints:
  - `/api/auth/**`
  - `/products`, `/products/**`
  - Swagger/OpenAPI endpoints
- Everything else requires a valid JWT bearer token.
- App is stateless (`SessionCreationPolicy.STATELESS`).

## Design notes / trade-offs
This project focuses on core backend flows first (auth, cart, order).
Product endpoints are currently public to keep demo/testing easy.
Security and validation are implemented, but this is still a learning/demo project, not a full production commerce system.
There is room to expand into inventory, payments, order status, and admin-specific access controls.

Author
Sandhya Rani
