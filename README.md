# 🚀 Chat App Backend (Spring Boot)

This is the backend service for the **Chat Application**, built with **Spring Boot**, providing RESTful APIs for authentication, messaging, friendship management, and event handling.

---

## 🌐 Related Projects

👉 **Frontend (React):**
https://github.com/tongducduy309/Chat-React

👉 **Face Recognition Service (FastAPI):**
https://github.com/tongducduy309/Face-Service-ChatApp

---

## 📌 Features

### 🔐 Authentication

* Register / Login with JWT
* Access & Refresh Token
* Secure API with Spring Security

### 👥 User Management

* Get user profile
* Search users
* Update user information

### 🤝 Friendship System

* Send friend request
* Accept / Reject request
* Cancel request
* Unfriend
* Block / Unblock user

### 💬 Chat System

* One-to-one messaging
* Conversation management
* Message history

### 📅 Event Management

* Create / Update / Delete events
* Calendar integration

---

## 🛠️ Tech Stack

* ☕ **Spring Boot**
* 🔐 **Spring Security + JWT**
* 🗄️ **JPA / Hibernate**
* 🐬 **MySQL / PostgreSQL**
* 📦 **Maven**
* 🌐 **REST API**
* 📄 **Lombok**

---

## 📂 Project Structure

```bash id="d55g6h"
src/main/java/com/gener/chat
│── controllers/     # REST Controllers
│── services/        # Business logic
│── repositories/     # JPA repositories
│── models/         # Database entities
│── dtos/            # Data transfer objects
│── configuration/         # Security, JWT config
│── exception/      # Custom exceptions
│── enums/          # Error codes, roles
```

---

## ⚙️ Setup & Installation

### 1. Clone repository

```bash id="zff1b5"
git clone https://github.com/tongducduy309/ChatApp-SpringBoot.git
cd ChatApp-SpringBoot
```

---

### 2. Configure database

Update `application.yml` or `application.properties`:

```yaml id="5nyhgi"
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/chat_app
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

### 3. Run application

```bash id="43h3h4"
mvn spring-boot:run
```

or run directly in IDE.

---

## 🔐 Authentication

This project uses **JWT (JSON Web Token)**:

* Access Token: used for API requests
* Refresh Token: used to renew access token

### Example Header:

```http id="af0p24"
Authorization: Bearer <your_token>
```

---

## 📡 API Endpoints

### 🔐 Auth

```http id="3lgz3m"
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/refresh
```

---

### 👤 User

```http id="d77k9l"
GET    /api/users/me
GET    /api/users/{id}
GET    /api/users/search
```

---

### 🤝 Friendship

```http id="kq3bq9"
POST   /api/friends/{id}/request
POST   /api/friends/{id}/accept
POST   /api/friends/{id}/reject
POST   /api/friends/{id}/block
DELETE /api/friends/{id}
```

---

### 💬 Chat

```http id="8qv1k6"
GET    /api/conversations
GET    /api/messages/{conversationId}
POST   /api/messages
```

---

### 📅 Event

```http id="h5n4zn"
GET    /api/events
POST   /api/events
PUT    /api/events/{id}
DELETE /api/events/{id}
```

---

## ⚠️ Error Handling

Custom error system using `ErrorCode` enum:

```java id="9xav5l"
USER_NOT_FOUND
TOKEN_EXPIRED
ACCESS_DENIED
FRIEND_REQUEST_ALREADY_SENT
```

All errors return standardized response:

```json id="z1m6el"
{
  "code": 404,
  "message": "User not found"
}
```

---

## 🔄 Integration

This backend integrates with:

* React frontend (REST API)
* FastAPI face recognition service

---

## 🚧 Future Improvements

* 🔌 WebSocket (real-time chat)
* 📹 Video call (WebRTC)
* 🔔 Push notification (Firebase)
* 🧠 AI chatbot integration
* 📊 Admin dashboard

---

## 🤝 Contributing

```bash id="m50e7x"
git checkout -b feature/your-feature
git commit -m "Add feature"
git push origin feature/your-feature
```

---

## 👨‍💻 Author

**Tong Duc Duy**

* GitHub: https://github.com/tongducduy309

