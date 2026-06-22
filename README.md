# 📦 Orders REST API

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-green?style=flat-square&logo=swagger)](https://swagger.io/)
[![Maven](https://img.shields.io/badge/Maven-3.9.5-C71A36?style=flat-square&logo=apache-maven)](https://maven.apache.org/)

A complete **REST API for order management** built with **Spring Boot 3**, **JPA**, **MySQL**, **Flyway**, and **Swagger/OpenAPI 3.0**. 

The API provides full CRUD operations for **Products**, **Customers**, and **Orders** with order line management, validation, and comprehensive API documentation via interactive Swagger UI.

---

## 🚀 Features

✅ **Complete CRUD Operations**
- Products management
- Customers management  
- Orders management with line items
- Order validation with VIP discount logic

✅ **Advanced Order Management**
- Add/remove order lines
- Update order line quantities with automatic stock management
- Validate orders with automatic total calculation
- VIP customer discount (10% reduction)

✅ **API Documentation**
- Swagger UI for interactive API exploration
- OpenAPI 3.0 specification auto-generated
- Comprehensive endpoint documentation with examples

✅ **Authentication & Security**
- JWT-based authentication (Bearer token)
- Login and registration endpoints
- Stateless session management
- Password encryption with BCrypt
- Role-based access (USER / ADMIN)

✅ **Production Ready**
- Flyway database migrations
- Jakarta Bean Validation
- Global exception handling
- Transactional boundaries
- Spring Data JPA with pagination
- MySQL 8.0 support

✅ **Docker Support**
- Multi-stage Dockerfile for optimized builds
- Docker Compose for local development
- Environment-based configuration

---

## 🏗️ Architecture

```
rest-api-orders/
├── src/main/java/com/luqman/rest_api_orders/
│   ├── controllers/           # REST endpoints
│   ├── services/              # Business logic
│   ├── repositories/          # Data access layer
│   ├── entities/              # JPA entities
│   ├── dtos/                  # Data Transfer Objects
│   ├── enums/                 # Enumerations (OrderStatus)
│   ├── exceptions/            # Exception handling
│   ├── Application.java       # Spring Boot entry point
│   └── DataInitializer.java   # Local seed data
├── src/main/resources/
│   ├── application.yml        # Main configuration
│   ├── application-local.yml  # Local profile config
│   └── db/migration/          # Flyway migrations
├── Dockerfile                 # Multi-stage Docker build
├── docker-compose.yml         # Local dev environment
└── pom.xml                    # Maven dependencies
```

---

## 📋 Technology Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.5.14 |
| **ORM** | Spring Data JPA / Hibernate |
| **Database** | MySQL 8.0 |
| **Migrations** | Flyway |
| **Validation** | Jakarta Bean Validation |
| **API Docs** | Springdoc OpenAPI 3.0 |
| **Security** | Spring Security / JWT (jjwt) |
| **Build** | Maven 3.9.5 |
| **Containerization** | Docker & Docker Compose |
| **Utilities** | Lombok |

---

## 🔌 API Endpoints

### Authentication (`/auth`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | Authenticate and get JWT token |
| POST | `/auth/register` | Create a new user account |

**Example - Login:**
```bash
curl -X POST http://localhost:8633/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**Response:**
```json
{"token": "eyJhbGciOiJIUzI1NiJ9..."}
```

**Example - Register:**
```bash
curl -X POST http://localhost:8633/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "newuser", "password": "password123"}'
```

All other endpoints require a valid JWT in the `Authorization` header:
```bash
curl -H "Authorization: Bearer <token>" http://localhost:8633/products
```

---

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/products` | List all products (paginated) |
| GET | `/products/{id}` | Get product by ID |
| POST | `/products` | Create new product |
| PUT | `/products/{id}` | Update product |
| DELETE | `/products/{id}` | Delete product |

**Example Request:**
```bash
curl -X POST http://localhost:8633/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Pro",
    "description": "High-performance laptop",
    "weight": 1.5,
    "price": 1299.99,
    "stock": 10
  }'
```

---

### Customers (`/customers`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/customers` | List all customers (paginated) |
| GET | `/customers/{id}` | Get customer by ID |
| POST | `/customers` | Create new customer |
| PUT | `/customers/{id}` | Update customer |
| DELETE | `/customers/{id}` | Delete customer |

**Example Request:**
```bash
curl -X POST http://localhost:8633/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Luqman",
    "lastName": "Diallo",
    "address": "123 Main St",
    "vip": true
  }'
```

---

### Orders (`/orders`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/orders` | List all orders (paginated) |
| GET | `/orders/{id}` | Get order by ID |
| POST | `/orders` | Create new order |
| PUT | `/orders/{id}` | Update order (change customer) |
| DELETE | `/orders/{id}` | Delete order |
| POST | `/orders/{id}/lines` | Add line to order |
| GET | `/orders/{id}/lines` | List order lines |
| PUT | `/orders/{id}/lines/{lineId}` | Update line quantity |
| DELETE | `/orders/{id}/lines/{lineId}` | Remove line from order |
| POST | `/orders/{id}/validate` | Validate order & calculate total |

**Example - Create Order:**
```bash
curl -X POST http://localhost:8633/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": 1}'
```

**Example - Add Line to Order:**
```bash
curl -X POST http://localhost:8633/orders/1/lines \
  -H "Content-Type: application/json" \
  -d '{"productId": 5, "quantity": 2}'
```

**Example - Validate Order:**
```bash
curl -X POST http://localhost:8633/orders/1/validate \
  -H "Content-Type: application/json"
```

---

## 🎯 Quick Start

### Prerequisites

- **Java 17+** ([Download](https://adoptium.net/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **MySQL 8.0+** ([Download](https://dev.mysql.com/downloads/mysql/))
- **Docker & Docker Compose** (optional)

### Local Setup (Development)

#### 1. Clone the Repository
```bash
git clone https://github.com/SOULEYMANEHAMANEADJI/orders-api.git
cd rest-api-orders
```

#### 2. Configure Database Connection

Edit `.env` file (or modify `application-local.yml`):
```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3307/rest_api_orders_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=Admin$123
SPRING_PROFILES_ACTIVE=local
```

#### 3. Build the Application
```bash
mvn clean package -DskipTests
```

#### 4. Run Locally

**Option A: Using Maven**
```bash
mvn spring-boot:run
```

**Option B: Using PowerShell Script (Windows)**
```powershell
.\run-local.ps1
```

**Option C: Using JAR**
```bash
java -jar target/rest-api-orders-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

#### 5. Access the Application

- **Swagger UI**: http://localhost:8633/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8633/v3/api-docs
- **API Home**: http://localhost:8633/

---

### Docker Setup (Production-like)

#### 1. Build and Start with Docker Compose
```bash
docker-compose up -d
```

This will start:
- **MySQL 8.0** database on port `3307`
- **REST API** on port `8633`

#### 2. Verify Services
```bash
docker-compose ps
```

#### 3. View Logs
```bash
docker-compose logs -f rest-api-orders
```

#### 4. Stop Services
```bash
docker-compose down
```

---

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run with Coverage
```bash
mvn test jacoco:report
# View coverage report in: target/site/jacoco/index.html
```

### Test Sample Endpoints (after starting the app)

```bash
# List products
curl http://localhost:8633/products

# Create product
curl -X POST http://localhost:8633/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Mouse","price":25.00,"stock":100}'

# Get Swagger UI
open http://localhost:8633/swagger-ui.html
```

---

## 📊 Database Schema

### Entities Diagram

```
Customer (1) ──────────── (*) Order
   │
   └── VIP status (boolean) → applies 10% discount

Order (1) ──────────── (*) OrderLine
   │
   ├── Status (DRAFT, CONFIRMED)
   ├── Total Amount (calculated on validation)
   └── Lines (items in order)

OrderLine (*) ──────────── (1) Product
   │
   ├── Quantity
   ├── Unit Price
   └── Line Total (quantity × unit price)

Product
   ├── Stock (decreases when line added)
   └── Reserved Stock (managed during order operations)
```

### Main Tables

| Table | Purpose |
|-------|---------|
| `customers_tbl` | Customer information with VIP status |
| `products_tbl` | Product catalog with inventory |
| `orders_tbl` | Orders with status and totals |
| `order_lines_tbl` | Line items in orders |

---

## ⚙️ Configuration

### Application Properties

**`application.yml`** - Default configuration
```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3307/rest_api_orders_db
    username: root
    password: Admin$123
  jpa:
    hibernate:
      ddl-auto: update

server:
  port: 8633

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

**`application-local.yml`** - Local development profile
- Enhanced logging
- Database seed data via `DataInitializer`

### Environment Variables

You can override defaults using environment variables:
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/orders_db
export SPRING_DATASOURCE_USERNAME=user
export SPRING_DATASOURCE_PASSWORD=password
export JWT_SECRET=your-256-bit-secret-key-here
export JWT_EXPIRATION_MS=86400000
export SPRING_PROFILES_ACTIVE=local
```

---

## 🔒 Validation & Error Handling

### Validation Rules

- **Product**: name (required), price (positive), stock (non-negative)
- **Customer**: firstName, lastName (required), address (optional), vip (boolean)
- **Order**: customerId (required), lines (at least one for validation)
- **OrderLine**: productId, quantity (positive)

### Error Responses

All errors return standardized JSON:
```json
{
  "error": "Order with id 999 not found",
  "timestamp": "2024-06-20T10:30:45.123Z",
  "status": 404
}
```

### HTTP Status Codes

| Status | Scenario |
|--------|----------|
| **200** | Success (GET, PUT) |
| **201** | Created successfully (POST) |
| **204** | No Content (DELETE, validation endpoints) |
| **400** | Validation error |
| **404** | Resource not found |
| **500** | Server error |

---

## 📈 Performance Features

- **Pagination**: All list endpoints support Spring Data pagination
- **Lazy Loading**: Order lines loaded on demand
- **Transactional Boundaries**: `@Transactional` ensures data consistency
- **Optimistic Locking**: Version column on Product entity (conflict detection)

---

## 🔄 Data Flow Example

**Creating and Validating an Order:**

```
1. POST /orders                    → Create empty order (DRAFT)
   ↓
2. POST /orders/{id}/lines (x2)   → Add 2 products to order
   ↓
3. GET /orders/{id}/lines          → Verify line items
   ↓
4. POST /orders/{id}/validate      → Calculate total + apply VIP discount
   ↓
5. Order status becomes CONFIRMED  → Ready for fulfillment
```

---

## 📝 Development Notes

### Naming Convention
- All code, routes, and database entities use English names (Product, Customer, Order)
- Routes: `/products`, `/customers`, `/orders`
- Database tables use English entity names

### Seed Data
When running with `local` profile, `DataInitializer` automatically seeds:
- 2 sample customers (1 VIP, 1 regular)
- 3 sample products (Laptop, Mouse, Keyboard)

### Stock Management
- Stock decreases when a line is added to an order
- Stock increases when a line is removed
- Quantity changes adjust stock accordingly
- Prevents negative stock (throws `BusinessException`)

---

## 🚀 Deployment

### Build Production JAR
```bash
mvn clean package -DskipTests -Pprod
```

### Run with Production Profile
```bash
java -jar target/rest-api-orders-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:mysql://prod-db:3306/orders \
  --spring.datasource.username=produser \
  --spring.datasource.password=prodpass
```

### Docker Deployment
```bash
docker build -t orders-api:latest .
docker run -d \
  --name orders-api \
  -p 8633:8633 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/orders \
  orders-api:latest
```

---

## 📚 API Documentation

Interactive API documentation is automatically available:
- **Swagger UI**: http://localhost:8633/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8633/v3/api-docs
- **OpenAPI YAML**: http://localhost:8633/v3/api-docs.yaml

Try requests directly from Swagger UI interface.

---

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👤 Author

**Souleyamane Hamane Adji**
- GitHub: [@SOULEYMANEHAMANEADJI](https://github.com/SOULEYMANEHAMANEADJI)
- Email: souleymane.hamane.adji@example.com

---

## 🆘 Troubleshooting

### Issue: "Connection refused" to MySQL
**Solution**: 
- Ensure MySQL is running on port 3307
- Check `.env` file for correct credentials
- Use Docker Compose: `docker-compose up -d`

### Issue: Swagger UI returns 404
**Solution**:
- Application must be running (`mvn spring-boot:run`)
- URL is exactly: `http://localhost:8633/swagger-ui.html` (no trailing slash)
- Check firewall/antivirus not blocking port 8633

### Issue: Tests fail with database errors
**Solution**:
- Run with H2 in-memory DB for tests
- Or ensure MySQL test database exists
- Run: `mvn clean test`

### Issue: Stock goes negative
**Solution**:
- This is prevented by `Product.reserveStock()` validation
- Check product has sufficient stock before adding order lines
- Endpoint will return `400 Bad Request` with error message

---

## 📞 Support

For issues, questions, or suggestions:
1. Check [GitHub Issues](https://github.com/SOULEYMANEHAMANEADJI/orders-api/issues)
2. Open a new issue with detailed description
3. Include error logs and reproduction steps

---

**Last Updated**: June 2026  
**Version**: 0.0.1-SNAPSHOT  
**Status**: Ready for Production ✅

