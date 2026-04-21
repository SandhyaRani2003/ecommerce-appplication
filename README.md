# E-Commerce REST API

A full-featured, production-ready REST API for an e-commerce platform built with **Spring Boot 3** and modern Java development practices.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Security](#security)

---

## ✨ Features

### Authentication & Authorization
- ✅ JWT Token-based Authentication
- ✅ User Registration with encrypted passwords
- ✅ User Login with JWT token generation
- ✅ Role-based Access Control (USER role)

### Product Management
- ✅ View all products
- ✅ Create new products
- ✅ Price management

### Shopping Cart
- ✅ Add/remove items from cart
- ✅ Update item quantities
- ✅ View cart with total calculations
- ✅ Per-user isolated carts

### Order Management
- ✅ Checkout from cart
- ✅ View order history
- ✅ Order details with items and prices
- ✅ Auto cart clear after checkout

### API Documentation
- ✅ Swagger UI - Interactive documentation
- ✅ OpenAPI 3.0 specification

---

## 🛠 Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17+ |
| Framework | Spring Boot | 3.2.0 |
| Database | MySQL | 5.7+ |
| ORM | Spring Data JPA | 3.2.0 |
| Security | Spring Security + JWT | 6.x |
| Testing | JUnit 5 + Mockito | Latest |
| Build Tool | Maven | 3.8+ |

---

## 📦 Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- MySQL 5.7 or higher

---

## 🚀 Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/ecommerce-api.git
cd ecommerce-api
```

### 2. Configure MySQL and JWT (Environment Variables)
Create database:
```sql
CREATE DATABASE ecommerce;
```

Set environment variables (recommended for GitHub-safe setup):
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/ecommerce?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="root"
$env:JWT_SECRET="ChangeThisToAStrongSecretKeyWithAtLeast32Chars"
$env:JWT_EXPIRATION_MS="86400000"
```

The app also has safe local fallbacks in `src/main/resources/application.properties` if env vars are not set.

### 3. Run Application
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Mac/Linux
./mvnw spring-boot:run
```

Application starts on: **http://localhost:8080**

---

## 📚 API Documentation

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/api-docs

---

## 🔌 Key API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Products
- `GET /products` - Get all products
- `GET /products/{id}` - Get product by ID
- `POST /products` - Create product

### Cart
- `GET /api/cart` - View cart
- `POST /api/cart/items` - Add to cart
- `PUT /api/cart/items/{itemId}` - Update quantity
- `DELETE /api/cart/items/{itemId}` - Remove item

### Orders
- `POST /api/orders/checkout` - Create order
- `GET /api/orders` - View order history

---

## 🧪 Testing

Run all tests:
```bash
./mvnw clean test
```

**Test Coverage:**
- ✅ ProductServiceTest (6 tests)
- ✅ CartServiceTest (6 tests)
- ✅ OrderServiceTest (6 tests)
- ✅ AuthServiceTest (7 tests)
- **Total: 25+ test cases**

---

## 🔒 Security Features

- JWT Token-based authentication
- Password encryption (Bcrypt)
- CORS configuration
- Input validation
- SQL injection prevention
- Security headers

---

## 📁 Project Structure

```
ecommerce-api/
├── src/main/java/com/example/ecommerce/
│   ├── controller/      # REST endpoints
│   ├── service/         # Business logic
│   ├── repository/      # Database access
│   ├── model/           # Entity classes
│   ├── dto/             # Data transfer objects
│   ├── exception/       # Exception handling
│   ├── security/        # JWT & security
│   └── config/          # Configuration
├── src/test/            # Unit tests
├── pom.xml
└── README.md
```

---

## 🚀 Getting Started Workflow

1. **Register User:** POST /api/auth/register
2. **Login:** POST /api/auth/login → Get JWT token
3. **Create Products:** POST /products
4. **View Products:** GET /products
5. **Add to Cart:** POST /api/cart/items
6. **Checkout:** POST /api/orders/checkout
7. **View Orders:** GET /api/orders

---

## 📈 Future Enhancements

- Product search and filtering
- Order status updates
- Email notifications
- Payment integration
- Product reviews
- Inventory management

---

## 👨‍💻 Author

Your Name - [GitHub](https://github.com/yourusername) | [LinkedIn](https://linkedin.com/in/yourprofile)

---

**Built with ❤️ by a Java Developer**
**Last Updated: April 21, 2026**
