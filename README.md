# 🏋️ Fitness App

A microservices-based fitness tracking application built with **Spring Boot, Spring Cloud, React, MongoDB, PostgreSQL, RabbitMQ, Keycloak, and Google Gemini AI**.

Users can authenticate securely, track their fitness activities such as running, cycling, swimming, and walking, and receive AI-generated fitness recommendations based on their activity history.

---

## 📑 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Application Flow](#-application-flow)
- [Microservices](#-microservices)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Project Structure](#-project-structure)
- [Local Setup](#-local-setup)
- [Keycloak Configuration](#-keycloak-configuration)
- [Startup Order](#-startup-order)
- [Application URLs](#-application-urls)
- [API Endpoints](#-api-endpoints)
- [Authentication](#-authentication)
- [Automatic User Synchronization](#-automatic-user-synchronization)
- [Service Discovery](#-service-discovery)
- [RabbitMQ Communication](#-rabbitmq-communication)
- [AI Recommendation Flow](#-ai-recommendation-flow)
- [Database Architecture](#-database-architecture)
- [Eureka Dashboard](#-eureka-dashboard)
- [Testing the Application](#-testing-the-application)
- [Troubleshooting](#-troubleshooting)
- [Development Notes](#-development-notes)
- [Future Improvements](#-future-improvements)
- [Author](#-author)
- [License](#-license)

---

## 🚀 Features

- 🔐 User authentication with Keycloak
- 🔑 OAuth2 Authorization Code + PKCE
- 🛡️ JWT-based API security
- 👤 Automatic user synchronization with Keycloak
- 🏃 Fitness activity tracking
- 🚴 Support for multiple activity types
- 📊 Activity history and activity details
- 🤖 AI-powered fitness recommendations using Google Gemini
- 🔄 Event-driven communication using RabbitMQ
- 🗄️ PostgreSQL for user data
- 🍃 MongoDB for activity and recommendation data
- 🌐 Spring Cloud API Gateway
- 🔎 Eureka service discovery
- ⚙️ Spring Cloud Config Server
- ⚛️ React frontend with Vite
- 🎨 Material UI
- 📦 Redux Toolkit
- 🔗 REST APIs
- ⚖️ Load-balanced communication between microservices

---

## 🏗️ Architecture

```text
                         ┌──────────────────────────┐
                         │      React Frontend       │
                         │      localhost:5173       │
                         │                            │
                         │  React + Vite + MUI        │
                         │  Redux Toolkit + Axios     │
                         └────────────┬───────────────┘
                                      │
                                      │ OAuth2 / PKCE
                                      ▼
                         ┌──────────────────────────┐
                         │        Keycloak           │
                         │      localhost:8181       │
                         │                            │
                         │  Realm: fitness-oauth2     │
                         └────────────┬───────────────┘
                                      │
                                      │ JWT
                                      ▼
                         ┌──────────────────────────┐
                         │       API Gateway          │
                         │      localhost:8085        │
                         │                            │
                         │  Spring Cloud Gateway       │
                         │  WebFlux + JWT Validation   │
                         └────────────┬───────────────┘
                                      │
                                      │ Service Discovery
                                      ▼
                         ┌──────────────────────────┐
                         │      Eureka Server         │
                         │      localhost:8761        │
                         └────────────┬───────────────┘
                                      │
                   ┌──────────────────┼──────────────────┐
                   │                  │                  │
                   ▼                  ▼                  ▼
        ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
        │   User Service    │ │ Activity Service  │ │    AI Service     │
        │  localhost:8081   │ │  localhost:8082   │ │  localhost:8083   │
        │                    │ │                    │ │                    │
        │  Spring Boot       │ │  Spring Boot       │ │  Spring Boot       │
        │  Spring Data JPA   │ │  MongoDB           │ │  MongoDB           │
        │                    │ │  RabbitMQ Producer │ │  RabbitMQ Consumer │
        └────────┬───────────┘ └────────┬───────────┘ └────────┬───────────┘
                 │                      │                      │
                 ▼                      ▼                      ▼
          ┌─────────────┐        ┌─────────────┐        ┌─────────────┐
          │ PostgreSQL  │        │   MongoDB   │        │   MongoDB   │
          └─────────────┘        └──────┬──────┘        └─────────────┘
                                         │
                                         │ Event
                                         ▼
                                   ┌─────────────┐
                                   │  RabbitMQ   │
                                   │ localhost:  │
                                   │    5672     │
                                   └──────┬──────┘
                                          │
                                          ▼
                                   ┌─────────────┐
                                   │ AI Service  │
                                   │             │
                                   │ Gemini API  │
                                   └─────────────┘


                         ┌──────────────────────────┐
                         │      Config Server         │
                         │      localhost:8080        │
                         │                            │
                         │ Centralized Configuration  │
                         └──────────────────────────┘
```

---

## 🔄 Application Flow

### User Authentication

```text
React Frontend
      │
      │ Login
      ▼
   Keycloak
      │
      │ OAuth2 Authorization Code + PKCE
      ▼
React Frontend
      │
      │ JWT Access Token
      ▼
 API Gateway
      │
      │ Validate JWT
      ▼
Keycloak JWK Endpoint
      │
      ▼
Authenticated Request
```

### Adding a Fitness Activity

```text
User
 │
 │ Add Activity
 ▼
React Frontend
 │
 │ POST /api/activities
 │ Authorization: Bearer <JWT>
 ▼
API Gateway
 │
 │ JWT Validation
 ▼
Eureka Service Discovery
 │
 │ ACTIVITY-SERVICE
 ▼
Activity Service
 │
 ├──────────────► MongoDB
 │                  │
 │                  │ Save Activity
 │                  ▼
 │
 └──────────────► RabbitMQ
                    │
                    │ Activity Event
                    ▼
                 AI Service
                    │
                    │ Send Activity Data
                    ▼
               Google Gemini
                    │
                    │ AI Recommendation
                    ▼
                 MongoDB
```

---

## 🧩 Microservices

| Service | Port | Technology | Purpose |
|---|---|---|---|
| Eureka Server | 8761 | Spring Cloud Netflix Eureka | Service discovery |
| Config Server | 8080 | Spring Cloud Config | Centralized configuration |
| API Gateway | 8085 | Spring Cloud Gateway + WebFlux | API routing and security |
| User Service | 8081 | Spring Boot + JPA | User management |
| Activity Service | 8082 | Spring Boot + MongoDB | Activity management |
| AI Service | 8083 | Spring Boot + MongoDB | AI recommendations |
| Frontend | 5173 | React + Vite + MUI | User interface |
| Keycloak | 8181 | Keycloak | Authentication |
| PostgreSQL | 5432 | PostgreSQL | User database |
| MongoDB | 27017 | MongoDB | Activity/recommendation database |
| RabbitMQ | 5672 | RabbitMQ | Event messaging |

RabbitMQ Management UI: [http://localhost:15672](http://localhost:15672)

---

## 🛠️ Technology Stack

**Backend**
- Java
- Spring Boot
- Spring Cloud
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Spring Cloud Config
- Spring Security
- OAuth2 Resource Server
- JWT
- Spring Data JPA
- Spring Data MongoDB
- WebFlux
- WebClient
- Lombok
- Maven

**Frontend**
- React
- React Router
- Vite
- Material UI
- Redux Toolkit
- Axios
- react-oauth2-code-pkce

**Databases**
- PostgreSQL
- MongoDB

**Messaging**
- RabbitMQ

**Authentication**
- Keycloak
- OAuth2
- JWT
- PKCE

**AI**
- Google Gemini API

---

## 📋 Prerequisites

Install the following before running the project:

- Java 17 or higher
- Maven
- Node.js 18+
- npm
- PostgreSQL
- MongoDB
- RabbitMQ
- Keycloak
- Google Gemini API Key

---

## 📁 Project Structure

```text
Fitness/
│
├── eureka/
│   └── eureka/
│       ├── src/
│       └── pom.xml
│
├── configserver/
│   └── configserver/
│       ├── src/
│       └── pom.xml
│
├── gateway/
│   └── gateway/
│       ├── src/
│       └── pom.xml
│
├── userservice/
│   └── userservice/
│       ├── src/
│       └── pom.xml
│
├── activityservice/
│   └── activityservice/
│       ├── src/
│       └── pom.xml
│
├── aiservice/
│   └── aiservice/
│       ├── src/
│       └── pom.xml
│
└── Fitness-App-Frontend/
    ├── src/
    ├── public/
    ├── package.json
    └── vite.config.js
```

---

## ⚙️ Local Setup

### 1. Start PostgreSQL

Make sure PostgreSQL is running on `localhost:5432`.

Create the required database:

```sql
fitness_user_db
```

The User Service uses PostgreSQL to store user information.

### 2. Start MongoDB

Make sure MongoDB is running on `localhost:27017`.

MongoDB is used by:
- Activity Service
- AI Service

### 3. Start RabbitMQ

Start RabbitMQ on `localhost:5672`.

Default credentials:

```text
Username: guest
Password: guest
```

RabbitMQ Management UI: [http://localhost:15672](http://localhost:15672)

### 4. Start Keycloak

Start Keycloak on [http://localhost:8181](http://localhost:8181).

Open the Keycloak administration console and create the following realm:

```text
fitness-oauth2
```

See the [Keycloak Configuration](#-keycloak-configuration) section below for client and user setup.

### 5. Start Eureka Server

```bash
cd eureka
mvn spring-boot:run
```

Eureka will be available at: [http://localhost:8761](http://localhost:8761)

### 6. Start Config Server

```bash
cd configserver/configserver
mvn spring-boot:run
```

Config Server: [http://localhost:8080](http://localhost:8080)

### 7. Start User Service

```bash
cd userservice/userservice
mvn spring-boot:run
```

User Service: [http://localhost:8081](http://localhost:8081)

The service registers itself with Eureka.

### 8. Start Activity Service

```bash
cd activityservice/activityservice
mvn spring-boot:run
```

Activity Service: [http://localhost:8082](http://localhost:8082)

The service registers itself with Eureka. Activity data is stored in MongoDB.

### 9. Start AI Service

Configure the Google Gemini API credentials required by the application, then start:

```bash
cd aiservice/aiservice
mvn spring-boot:run
```

AI Service: [http://localhost:8083](http://localhost:8083)

The AI Service:
- Consumes activity events from RabbitMQ
- Processes the activity information
- Sends relevant information to Google Gemini
- Generates a fitness recommendation
- Stores the recommendation in MongoDB

### 10. Start API Gateway

```bash
cd gateway/gateway
mvn spring-boot:run
```

API Gateway: [http://localhost:8085](http://localhost:8085)

The Gateway handles:
- JWT validation
- Authentication
- Request routing
- Service discovery
- Load balancing
- User synchronization

### 11. Start React Frontend

```bash
cd Fitness-App-Frontend
npm install
npm run dev
```

Frontend: [http://localhost:5173](http://localhost:5173)

---

## 🔐 Keycloak Configuration

### Create Client

Create a client with:

```text
Client ID: oauth-pkce-client
```

Configure it as a public client.

**Client Configuration**

| Setting | Value |
|---|---|
| Client Type | Public |
| Standard Flow | Enabled |
| PKCE | S256 |
| Valid Redirect URI | `http://localhost:5173/*` |
| Web Origin | `http://localhost:5173` |

### Create a User

Create a user inside the `fitness-oauth2` realm with:
- First Name
- Last Name
- Email
- Password

The Gateway reads user information from the JWT and synchronizes the user with the User Service.

---

## 🚀 Startup Order

For local development, start the services in this order:

1. PostgreSQL
2. MongoDB
3. RabbitMQ
4. Keycloak
5. Eureka Server
6. Config Server
7. User Service
8. Activity Service
9. AI Service
10. API Gateway
11. React Frontend

---

## 🌐 Application URLs

| Component | URL |
|---|---|
| Frontend | http://localhost:5173 |
| API Gateway | http://localhost:8085 |
| Config Server | http://localhost:8080 |
| Eureka | http://localhost:8761 |
| User Service | http://localhost:8081 |
| Activity Service | http://localhost:8082 |
| AI Service | http://localhost:8083 |
| Keycloak | http://localhost:8181 |
| RabbitMQ | http://localhost:15672 |
| PostgreSQL | localhost:5432 |
| MongoDB | localhost:27017 |

---

## 🔗 API Endpoints

All frontend requests should go through the API Gateway.

**Base URL:** `http://localhost:8085/api`

### Activity APIs

**Get All Activities**

```http
GET /api/activities
```

Full URL: `http://localhost:8085/api/activities`

**Add Activity**

```http
POST /api/activities
```

Example request body:

```json
{
  "type": "RUNNING",
  "duration": 30,
  "caloriesBurned": 250,
  "additionalMetrics": {}
}
```

**Get Activity Details**

```http
GET /api/activities/{id}
```

Example: `http://localhost:8085/api/activities/123`

---

## 🔑 Authentication

The frontend uses **OAuth2 Authorization Code Flow + PKCE**.

1. The frontend obtains an access token from Keycloak.
2. The token is sent to the Gateway:

```http
Authorization: Bearer <JWT>
```

3. The Gateway validates the JWT using Keycloak.

---

## 👤 Automatic User Synchronization

The Gateway contains a custom `KeycloakUserSyncFilter`.

The filter:
1. Reads the Authorization header.
2. Extracts the JWT.
3. Reads user claims.
4. Extracts the Keycloak user ID.
5. Checks whether the user exists in the User Service.
6. Registers the user if required.
7. Adds the user ID to the downstream request.

Example header:

```http
X-User-Id: <keycloak-user-id>
```

This allows downstream services to identify the authenticated user.

---

## 🔄 Service Discovery

The application uses Eureka for service discovery.

The Gateway uses load-balanced routes:

```text
lb://USER-SERVICE
lb://ACTIVITY-SERVICE
lb://AI-SERVICE
```

Example flow:

```text
Frontend
   ↓
Gateway :8085
   ↓
Eureka :8761
   ↓
ACTIVITY-SERVICE
   ↓
Activity Service :8082
```

This avoids hardcoding downstream service addresses inside the Gateway.

---

## 📨 RabbitMQ Communication

Activity Service acts as a RabbitMQ producer. When a new activity is created:

```text
Activity Service
      │
      │ Save activity
      ▼
   MongoDB
      │
      │ Publish event
      ▼
   RabbitMQ
      │
      │ Consume event
      ▼
   AI Service
```

The AI Service processes the event and generates a recommendation.

---

## 🤖 AI Recommendation Flow

The AI Service receives activity information such as:

```json
{
  "type": "RUNNING",
  "duration": 30,
  "caloriesBurned": 250
}
```

The information is processed and sent to Google Gemini. Gemini generates a personalized recommendation, which is then stored in MongoDB.

---

## 🗄️ Database Architecture

### PostgreSQL

Used by: **User Service**

Database: `fitness_user_db`

### MongoDB

Used by: **Activity Service**, **AI Service**

Activity data and AI recommendations are stored in MongoDB.

### 🐰 RabbitMQ

RabbitMQ is used for asynchronous communication between:

```text
Activity Service
       ↓
    RabbitMQ
       ↓
   AI Service
```

RabbitMQ Management UI: [http://localhost:15672](http://localhost:15672)

---

## 🔍 Eureka Dashboard

After starting all services, open: [http://localhost:8761](http://localhost:8761)

You should see the following services with status `UP`:

- `USER-SERVICE`
- `ACTIVITY-SERVICE`
- `AI-SERVICE`
- `API-GATEWAY`

---

## 🧪 Testing the Application

1. Open [http://localhost:5173](http://localhost:5173)
2. Click **LOGIN**
3. Authenticate using Keycloak
4. After successful authentication, access `/activities`
5. Add a fitness activity, for example:
   - Type: `Running`
   - Duration: `30 minutes`
   - Calories: `250`
6. The request flows through:

```text
React
 ↓
API Gateway
 ↓
Eureka
 ↓
Activity Service
 ↓
MongoDB
 ↓
RabbitMQ
 ↓
AI Service
 ↓
Google Gemini
```

---

## 🛠️ Troubleshooting

### 401 Unauthorized

If you receive `401 Unauthorized`, check:
- Keycloak is running
- User is authenticated
- JWT is valid
- Frontend sends the JWT
- Gateway can reach Keycloak
- Keycloak realm is `fitness-oauth2`

The request should contain:

```http
Authorization: Bearer <token>
```

### 404 Not Found for Activities

Make sure the frontend calls the Gateway:

```text
✅ http://localhost:8085/api/activities
```

Do **not** call:

```text
❌ http://localhost:8083/api/activities
```

because port `8083` belongs to the AI Service.

### Eureka Service Not Showing

Open [http://localhost:8761](http://localhost:8761). If a service is missing:
- Check whether the service is running
- Check the Eureka URL
- Check the service logs
- Restart the service after Eureka is running

### MongoDB Connection Error

Make sure MongoDB is running: `localhost:27017`

### PostgreSQL Connection Error

Make sure PostgreSQL is running: `localhost:5432` and that `fitness_user_db` exists.

### RabbitMQ Connection Error

Make sure RabbitMQ is running: `localhost:5672`

Open the management UI: [http://localhost:15672](http://localhost:15672)

Default credentials: `guest / guest`

### Gateway Service Discovery Problem

Check Eureka first: [http://localhost:8761](http://localhost:8761)

Make sure `USER-SERVICE`, `ACTIVITY-SERVICE`, and `AI-SERVICE` are registered and showing `UP`.

---

## 💻 Development Notes

The frontend communicates with the backend **only** through the API Gateway:

```text
React :5173
     ↓
Gateway :8085
```

The frontend should not directly communicate with individual microservices.

```text
✅ http://localhost:8085/api/activities
❌ http://localhost:8082/api/activities
```

The Gateway handles service discovery and routing.

---

## 🔮 Future Improvements

- [ ] Dockerize the complete application
- [ ] Automated Keycloak realm import
- [ ] Swagger/OpenAPI documentation
- [ ] Unit testing
- [ ] Integration testing
- [ ] CI/CD pipeline
- [ ] Prometheus monitoring
- [ ] Grafana dashboards
- [ ] Distributed tracing
- [ ] Centralized logging
- [ ] Improved fitness analytics
- [ ] AI-based workout planning
- [ ] AI-based progress analysis
- [ ] Notification system
- [ ] User dashboard with charts
- [ ] Activity statistics and reports

---

## 👨‍💻 Author

**Adarsh Kumar Choubey**

Java | Spring Boot | Microservices | React | SQL | MongoDB | Docker

---

## 📄 License

This project is created for learning, portfolio, and demonstration purposes.
