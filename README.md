# TechIt E-Commerce

A modern full-stack e-commerce platform for tech products with secure payments, image management, and lightning-fast caching.

- **Live Demo** - https://techit-demo.netlify.app/

## 📂 Project Structure

```
root
│
├── frontend → React (Vite)
└── backend → Spring Boot 3.x
```

### 🔹 Frontend
React with modern UI tools.
```
Main folders:
- `pages/`
- `components/`
- `service/`
- `context/`
- `utils/`
- `styles/`
- `App.jsx`
- `main.jsx`
```

### 🔹 Backend
Spring Boot REST API.
```
Main packages:
- `config/` → Redis, JWT, Cloudinary config
- `controller/`
- `service/`
- `entity/`
- `repository/`
- `dto/`
- `exception/`
- `resources/`
  - `application.properties`
  - `application-dev.properties`
  - `application-prod.properties`
```

---

## 🚀 Technologies Used

### Frontend
- React 18+
- Vite
- Tailwind CSS
- Axios
- React Router

### Backend
- Java 21+
- Spring Boot 3.x
- JWT (Authentication)
- Redis 8.4.2 (Caching)
- Cloudinary (Image Upload)
- Xendit (Payments)
- MySQL 8.0+

---

## 🧪 Environment Variables

### Backend (`application.properties`)
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/techit_db
spring.datasource.username=techit_user
spring.datasource.password=your_password

# Frontend URL
techit.user.frontend.url=${TECHIT_USER_FE_URL}
techit.admin.frontend.url=${TECHIT_ADMIN_FE_URL}

# Redis
spring.redis.url=redis://localhost:6379
spring.redis.username=your_redis_username
spring.redis.password=your_redis_password
# JWT
jwt.secret=your_32_character_secret_key_here
jwt.expiration=86400000

# Cloudinary
cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret

# Xendit
xendit.secret.key=xnd_test_or_live_key
app.callback.success.url=http://localhost:5173/payment-success
app.callback.failure.url=http://localhost:5173/payment-failed
```

---

## 🌍 Deployment

### Backend
- Hosted on **Render** / **DBeaver**
- Database: **MySQL** / **PostgreSQL**
- Configuration: `application-prod.properties`

### Frontend
- Hosted on **Netlify**
- Build: `npm run build`

---

## 💻 Local Development Setup

### 1️⃣ Backend Setup

Clone and install:
```bash
git clone <repo>
cd backend
mvn clean install
```

Start Redis:
```bash
docker-compose up -d
```

Configure `application.properties` with your database and API keys.

Start Spring Boot:
```bash
mvn spring-boot:run
```

Backend runs on: `http://localhost:8080`

---

### 2️⃣ Frontend Setup

Navigate to frontend:
```bash
cd frontend
npm install
npm run dev
```

Frontend runs on: `http://localhost:5173`

---

## ✨ Features

- ✅ Product Catalog & Search
- ✅ Shopping Cart (Redis cached, 24h persistence)
- ✅ User Authentication (JWT)
- ✅ **Two Payment Methods:**
  - Xendit (Card payments: Visa, Mastercard, GCash)
  - Cash on Delivery (COD)
- ✅ Payment Method Selection Modal
- ✅ Order Management
- ✅ Image Upload (Cloudinary)
- ✅ Lightning-Fast Caching (Redis)
  - Products: 10 min TTL
  - Cart: 24 hour TTL
  - Search: 5 min TTL
- ✅ Admin Dashboard
- ✅ Responsive Design (Mobile-First)
- 🔜 Order Confirmation & Tracking
- 🔜 Mobile App

---

## 🔐 Security Features

- JWT Token Authentication
- Password Encryption (Bcrypt)
- CORS Protection
- Role-Based Access Control (Admin/User)
- Secure Payment Gateway (Xendit)

---

## 🧪 Testing

### Test Card (Xendit)
```
Card Number: 4111 1111 1111 1111
Expiry: Any future date
CVV: Any 3 digits
```

### Test COD
Select "Cash on Delivery" at checkout - no payment needed for testing.

---

## 🐛 Troubleshooting

**Redis Connection Error**
```bash
docker ps | grep redis
docker-compose restart redis
```

**Port Already in Use**
```bash
# Change port in application.properties
server.port=8081
```

**CORS Error**
```
Add to application.properties:
spring.web.cors.allowed-origins=http://localhost:5173
spring.web.cors.allowed-methods=*
spring.web.cors.allowed-headers=*
```

---

# API Documentation

## 📍 Base URL
```
http://localhost:8080/api/v1
```

---

## 👤 User API

### Authentication (UserController)

#### Register
```bash
POST /api/v1/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Login
```bash
POST /api/v1/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "role": "USER"
  }
}
```

---

## 📦 Products API

### Products (ProductController, HomeController)

#### List All Products (Cached - 10 min TTL)
```bash
GET /api/v1/products
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "MacBook Pro",
    "price": 1299.99,
    "stock": 15,
    "imageUrl": "https://cloudinary.com/...",
    "category": "Laptops",
    "brand": "Apple"
  }
]
```

#### Get Single Product (Cached - 10 min TTL)
```bash
GET /api/v1/products/{id}
```

---

## 🏷️ Categories API

### Categories (CategoryController)

#### List All Categories
```bash
GET /api/v1/categories
```

#### Get Single Category
```bash
GET /api/v1/categories/{id}
```

---

## 🎨 Brands API

### Brands (BrandController)

#### List All Brands
```bash
GET /api/v1/brands
```

#### Get Single Brand
```bash
GET /api/v1/brands/{id}
```

---

## 🛒 Cart API

### Cart (CartController)

#### Get Cart / Checkout View
```bash
GET /api/v1/carts/checkout
Authorization: Bearer {token}
```

#### Get All Cart Items
```bash
GET /api/v1/carts
Authorization: Bearer {token}
```

**Response:**
```json
{
  "id": 1,
  "userId": 42,
  "items": [
    {
      "id": 1,
      "productId": 10,
      "quantity": 2,
      "price": 299.99
    }
  ],
  "total": 599.98
}
```

#### Add to Cart
```bash
POST /api/v1/carts
Authorization: Bearer {token}
Content-Type: application/json

{
  "productId": 10,
  "quantity": 2
}
```

#### Update Cart Item Quantity
```bash
PUT /api/v1/carts/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "quantity": 3
}
```

#### Remove Item from Cart
```bash
DELETE /api/v1/carts/{id}
Authorization: Bearer {token}
```

#### Count Cart Products
```bash
GET /api/v1/carts/count
Authorization: Bearer {token}
```

**Response:**
```json
{
  "count": 5
}
```

---

## 📋 Orders API

### Orders (OrderController)

#### Create Order / Checkout
```bash
POST /api/v1/orders/checkout
Authorization: Bearer {token}
Content-Type: application/json

{
  "products": [
    {
      "productId": 10,
      "quantity": 2
    }
  ],
  "totalItems": 2,
  "totalPrice": 599.99,
  "paymentMethod": "xendit"
}
```

**Response for Xendit:**
```json
{
  "orderId": 123,
  "referenceId": "ORD-1234567890-42",
  "invoiceUrl": "https://app.xendit.co/web/invoices/...",
  "paymentMethod": "xendit",
  "message": "Redirecting to payment..."
}
```

**Response for COD:**
```json
{
  "orderId": 124,
  "referenceId": "ORD-1234567891-42",
  "invoiceUrl": null,
  "paymentMethod": "cod",
  "message": "Order placed! Our team will contact you soon."
}
```

#### List User Orders
```bash
GET /api/v1/orders
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "id": 123,
    "referenceId": "ORD-1234567890-42",
    "total": 599.99,
    "status": "PAID",
    "paymentMethod": "xendit",
    "createdAt": "2024-04-19T10:30:00Z",
    "products": [...]
  }
]
```

---

## 💳 Payment Webhook

### Payment Webhook (XenditWebhookController)

#### Xendit Webhook Endpoint
```bash
POST /api/v1/xendit/webhook
Content-Type: application/json

{
  "id": "5e2183a02b2cf10e6642fe45",
  "external_id": "ORD-1234567890-42",
  "user_id": "user_id_string",
  "status": "PAID",
  "paid_at": "2024-04-19T10:35:00Z",
  "amount": 599.99,
  "invoice_url": "https://app.xendit.co/..."
}
```

**Note:** This endpoint is called automatically by Xendit after payment completion.

---

## 👨‍💼 Admin API

### Admin Products (AdminProductController)

#### List All Products (Admin)
```bash
GET /api/v1/admin/products
Authorization: Bearer {admin_token}
```

#### Create Product
```bash
POST /api/v1/admin/products
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "MacBook Pro",
  "description": "Powerful laptop",
  "price": 1299.99,
  "stock": 15,
  "categoryId": 1,
  "brandId": 1,
  "imageUrl": "https://cloudinary.com/..."
}
```

#### Get Single Product
```bash
GET /api/v1/admin/products/{id}
Authorization: Bearer {admin_token}
```

#### Update Product
```bash
PUT /api/v1/admin/products/{id}
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "MacBook Pro M3",
  "price": 1499.99,
  "stock": 20
}
```

#### Delete Product
```bash
DELETE /api/v1/admin/products/{id}
Authorization: Bearer {admin_token}
```

---

### Admin Categories (AdminCategoryController)

#### List All Categories
```bash
GET /api/v1/admin/categories
Authorization: Bearer {admin_token}
```

#### Create Category
```bash
POST /api/v1/admin/categories
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "Laptops",
  "description": "Portable computers"
}
```

#### Update Category
```bash
PUT /api/v1/admin/categories/{id}
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "Gaming Laptops",
  "description": "High-performance laptops"
}
```

#### Delete Category
```bash
DELETE /api/v1/admin/categories/{id}
Authorization: Bearer {admin_token}
```

---

### Admin Brands (AdminBrandController)

#### List All Brands
```bash
GET /api/v1/admin/brands
Authorization: Bearer {admin_token}
```

#### Create Brand
```bash
POST /api/v1/admin/brands
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "Apple",
  "description": "Premium tech brand"
}
```

#### Update Brand
```bash
PUT /api/v1/admin/brands/{id}
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "Apple Inc",
  "description": "Leading tech company"
}
```

#### Delete Brand
```bash
DELETE /api/v1/admin/brands/{id}
Authorization: Bearer {admin_token}
```

---

### Admin Users & Dashboard (AdminController, UserController)

#### List All Users
```bash
GET /api/v1/admin/users
Authorization: Bearer {admin_token}
```

**Response:**
```json
[
  {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER",
    "createdAt": "2024-04-10T08:20:00Z"
  }
]
```
---

## 📁 Controllers Reference
### User API
- `UserController.java` - User registration, login, profile

### Product API
- `ProductController.java` - Product listing and search
- `HomeController.java` - Homepage products

### Shopping
- `CartController.java` - Cart management
- `OrderController.java` - Order creation and tracking
- `CategoryController.java` - Product categories
- `BrandController.java` - Product brands

### Payment
- `XenditWebhookController.java` - Payment confirmation webhook

### Admin API
- `AdminProductController.java` - Product management
- `AdminCategoryController.java` - Category management
- `AdminBrandController.java` - Brand management
- `AdminController.java` - Dashboard and user management

---

## 🔐 Authentication

All endpoints marked with `Authorization: Bearer {token}` require JWT token.

Get token by logging in:
```bash
POST /api/v1/login
```

Then use in headers:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```
---

## 🚀 Quick Start

```bash
# 1. Clone repo
git clone <repo>

# 2. Setup Backend
cd backend
mvn clean install
docker-compose up -d
mvn spring-boot:run

# 3. Setup Frontend (new terminal)
cd frontend
npm install
npm run dev
```

Visit `http://localhost:5173`

---
