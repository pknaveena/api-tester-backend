# API Tester

A full-stack web-based API testing tool inspired by applications like Postman.

The project allows users to authenticate, create environments, execute HTTP API requests, view execution history, and organize requests into collections.

The backend is built using **Java 21 and Spring Boot**, with **MySQL** for data persistence and **JWT-based authentication** for securing APIs. A **React frontend built with Vite** provides the user interface for interacting with the backend.

---

## 🚀 Features

### Authentication

* User registration
* User login
* JWT-based authentication
* Stateless Spring Security configuration
* Protected API endpoints
* Authenticated user-specific data

### API Request Execution

Supports executing HTTP requests such as:

* GET
* POST
* PUT
* DELETE
* PATCH

Request features include:

* URL
* Query parameters
* Headers
* Request body
* Bearer token authentication
* Basic authentication
* API key authentication
* Environment variables

Example environment variable:

```text
https://api.example.com/users/{{userId}}
```

The variable can be resolved from the selected environment before executing the request.

### Request History

The application stores API execution history for authenticated users.

History includes information such as:

* HTTP method
* Request URL
* Request headers
* Request body
* Response body
* HTTP status code
* Response time
* Response size
* Request timestamp

Sensitive information is masked before being stored where applicable.

### Collections

Requests can be organized into collections.

A collection can contain multiple collection items, allowing related API requests to be grouped together and executed when required.

### Environments

Users can create environments containing reusable variables.

Example:

```json
{
  "baseUrl": "https://api.example.com",
  "userId": "1001"
}
```

These variables can then be used in requests:

```text
{{baseUrl}}/users/{{userId}}
```

### API Documentation

The backend provides interactive API documentation using Swagger/OpenAPI.

Swagger can be used to:

* View available endpoints
* Understand request and response structures
* Test APIs
* Authorize using a JWT token

---

## 🔐 Security

Security is an important part of the application.

The backend uses:

* Spring Security
* JWT authentication
* Stateless authentication
* Password hashing
* Authorization for protected endpoints
* Input validation
* URL validation
* Sensitive-data masking
* Rate limiting
* SSRF-related URL protection

### JWT Authentication

After successful login, the server returns a JWT token.

The token can then be sent with protected requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

Spring Security validates the token before allowing access to protected endpoints.

---

## 🛡️ URL Validation

The API execution service validates request URLs before making outgoing requests.

Only:

```text
http://
https://
```

URLs are allowed.

Certain local/internal hosts are blocked to reduce the risk of server-side request forgery (SSRF).

Examples of blocked hosts include:

```text
localhost
127.0.0.1
0.0.0.0
```

---

## ⏱️ Rate Limiting

API execution requests are rate-limited to prevent excessive requests from a single user.

The current implementation uses an in-memory rate limiter.

Example configuration:

```text
10 requests / 60 seconds
```

This helps prevent accidental or excessive API execution.

---

## 📊 Monitoring

The application uses Spring Boot Actuator for application monitoring.

Available metrics include information related to:

* HTTP requests
* JVM memory
* CPU
* Application health
* API execution metrics

Prometheus is used to collect metrics.

Grafana can then be used to visualize the metrics through dashboards.

### Monitoring Architecture

```text
API Tester Backend
        |
        v
Spring Boot Actuator
        |
        v
Prometheus
        |
        v
Grafana
```

---

## 📈 Custom API Execution Metrics

The application also includes custom metrics for API executions.

These metrics help monitor:

* Total API executions
* Successful API executions
* Failed API executions

This makes it possible to monitor the usage of the API execution feature and identify failed requests.

---

## 🧪 Testing

The backend contains different types of tests.

### Unit Tests

Used to test individual components in isolation.

Examples include:

* Service tests
* Utility tests
* Validation tests

### Controller Tests

Spring MVC tests using:

```text
@WebMvcTest
MockMvc
```

These tests verify controller behavior without starting the complete application.

### Integration Tests

Integration tests verify multiple parts of the application working together.

Example flow:

```text
Register
   ↓
Login
   ↓
Get JWT
   ↓
Create Environment
   ↓
Execute API
   ↓
Verify Response
```

---

## 🛠️ Technology Stack

### Backend

| Technology           | Purpose                         |
| -------------------- | ------------------------------- |
| Java 21              | Programming language            |
| Spring Boot 4        | Backend framework               |
| Spring Web           | REST APIs                       |
| Spring Data JPA      | Database access                 |
| Spring Security      | Application security            |
| JWT                  | Authentication                  |
| MySQL                | Database                        |
| Maven                | Build and dependency management |
| Swagger / OpenAPI    | API documentation               |
| Spring Boot Actuator | Monitoring                      |
| Micrometer           | Application metrics             |
| Prometheus           | Metrics collection              |
| Grafana              | Metrics visualization           |
| Docker               | Containerization                |

### Frontend

The frontend is implemented as a separate React application.

| Technology                            | Purpose               |
| ------------------------------------- | --------------------- |
| React                                 | Frontend UI           |
| JavaScript                            | Programming language  |
| Vite                                  | Frontend build tool   |
| Tailwind CSS                          | Styling               |
| React Router                          | Client-side routing   |
| Axios / Fetch-based API communication | Backend communication |

The frontend communicates with the Spring Boot backend through REST APIs.

---

## 🖥️ Frontend

The React frontend provides the user interface for the API testing application.

Current frontend functionality includes areas such as:

* User authentication
* Dashboard
* API request execution
* Request configuration
* Response viewing
* Request history
* History filtering
* Environment management
* Collections
* Collection items

The frontend is developed separately from the Spring Boot backend and communicates with the backend through REST endpoints.

### Frontend Architecture

```text
React Frontend
      |
      | REST API
      v
Spring Boot Backend
      |
      v
MySQL
```

---

## 🏗️ Project Architecture

The backend follows a layered architecture.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Additional components are used for cross-cutting concerns such as:

```text
Security
Validation
Exception Handling
Metrics
Rate Limiting
Utilities
```

A simplified application architecture looks like:

```text
                    ┌──────────────────┐
                    │  React Frontend  │
                    │  Vite + React    │
                    └────────┬─────────┘
                             │
                             │ REST API
                             ▼
                    ┌──────────────────┐
                    │  Spring Boot     │
                    │  Backend         │
                    └────────┬─────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
          ▼                  ▼                  ▼
    Controllers          Services         Security/JWT
          │                  │
          │                  ▼
          │             Repositories
          │                  │
          │                  ▼
          │                MySQL
          │
          ▼
    API Execution
          │
          ▼
    External APIs
```

---

## 📁 Project Structure

### Backend

The main package structure is organized approximately as follows:

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── apitester/
    │           └── api_tester_backend/
    │               ├── controller/
    │               ├── service/
    │               ├── repository/
    │               ├── entity/
    │               ├── dto/
    │               ├── security/
    │               ├── exception/
    │               ├── util/
    │               └── config/
    │
    └── resources/
        └── application.properties
```

Tests are organized under:

```text
src/
└── test/
    └── java/
        └── com/
            └── apitester/
                └── api_tester_backend/
```

### Frontend

The React frontend is maintained as a separate application:

```text
api-tester-frontend/
├── src/
│   ├── components/
│   ├── context/
│   ├── pages/
│   └── ...
├── package.json
├── vite.config.js
└── index.html
```

---

## 🗄️ Database

The application currently uses MySQL.

Example database:

```text
api_tester
```

The database stores application data such as:

* Users
* Environments
* Collections
* Collection items
* API execution history

---

## ⚙️ Configuration

Application configuration is kept outside the source code wherever possible.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/api_tester
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

security.jwt.secret=${JWT_SECRET}
```

Sensitive values such as database passwords and JWT secrets should **not** be committed to Git.

Environment variables can be used instead.

---

## ▶️ Running the Backend Locally

### Prerequisites

Install:

* Java 21
* Maven
* MySQL 8+
* Git

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

---

### 1. Clone the Repository

```bash
git clone <repository-url>
```

Move into the project:

```bash
cd api-tester-backend
```

---

### 2. Create the Database

Create a MySQL database:

```sql
CREATE DATABASE api_tester;
```

---

### 3. Configure Environment Variables

Configure the required database and JWT values.

For example:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

---

### 4. Run the Application

Using Maven:

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8081
```

---

## 🌐 Running the Frontend

The frontend is developed using React and Vite.

After cloning the frontend repository, install the dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

The Vite development server runs on the configured local frontend port.

The frontend then communicates with the Spring Boot backend through its REST APIs.

---

## 📖 Swagger / OpenAPI

Once the backend is running, Swagger UI can be used to explore and test the REST APIs.

```text
http://localhost:8081/swagger-ui/index.html
```

After logging in:

1. Register a user.
2. Login.
3. Copy the JWT token.
4. Open Swagger's **Authorize** button.
5. Enter the token using the Bearer authentication scheme.
6. Call protected endpoints.

---

## 🐳 Running with Docker

The backend can also be containerized using Docker.

Build the application:

```bash
./mvnw clean package
```

Build the Docker image:

```bash
docker build -t api-tester-backend .
```

Run the container:

```bash
docker run -p 8081:8080 api-tester-backend
```

Notice the port mapping:

```text
8081:8080
│    │
│    └── Container port
└─────── Host port
```

Therefore, from the host machine the application is accessed using:

```text
http://localhost:8081
```

Inside the Docker network, other containers communicate with the backend using its container port.

---

## 📊 Monitoring with Docker

The monitoring setup can contain:

```text
API Tester Backend
       │
       ▼
   Prometheus
       │
       ▼
    Grafana
```

Prometheus can scrape the backend metrics endpoint:

```text
/actuator/prometheus
```

When using Docker Compose, Prometheus can communicate with the backend using:

```text
app:8080
```

Here:

```text
app
```

is the Docker Compose service name.

And:

```text
8080
```

is the backend's **container port**, not the host port.

---

## 🔄 API Request Flow

A simplified API execution flow is:

```text
Client
  ↓
JWT Authentication
  ↓
Rate Limit Check
  ↓
Request Validation
  ↓
Environment Variable Resolution
  ↓
URL / Query / Header / Body Resolution
  ↓
Authentication Handling
  ↓
External API Request
  ↓
Response
  ↓
Save History
  ↓
Return Response to Client
```

---

## 📚 Main API Areas

The backend provides REST endpoints for areas such as:

```text
/api/auth
/api/environments
/api/api-execution
/api/history
/api/collections
/api/collection-items
```

The exact endpoints and request/response models can be explored through Swagger/OpenAPI.

---

## 🧹 Error Handling

The application uses centralized exception handling to provide consistent API error responses.

Validation errors and application-specific errors are converted into appropriate HTTP responses.

Examples include:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
500 Internal Server Error
```

---

## 🔑 HTTP Status Codes

The application follows standard HTTP status semantics.

### 400 Bad Request

The request data is invalid.

Examples:

```text
Invalid URL
Missing required field
Invalid environment
```

### 401 Unauthorized

The user is not authenticated.

Examples:

```text
JWT token missing
JWT token invalid
JWT token expired
```

### 403 Forbidden

The user is authenticated but does not have permission to access the requested resource.

---

## 🎯 Project Goals

The main goals of this project are:

* Build a practical REST API testing tool
* Develop a full-stack application using Java and React
* Learn Spring Boot development
* Implement secure JWT authentication
* Practice REST API design
* Work with relational databases
* Learn API request execution
* Implement request history
* Organize requests using collections
* Manage reusable environment variables
* Practice automated testing
* Learn Docker containerization
* Learn application monitoring
* Understand Prometheus and Grafana
* Build a React-based frontend

---

## 🗺️ Future Improvements

Planned improvements include:

* Import/export requests
* Improved request/response visualization
* Additional monitoring dashboards
* Additional automated tests
* Further security improvements
* Additional API testing features

---

## 👨‍💻 Author

**Naveena P.K.**

Software Developer

This project was created as a hands-on project to learn and demonstrate modern Java backend development, REST APIs, Spring Security, testing, React, Docker, and application monitoring.

### Request History

The application stores API execution history for authenticated users.

History includes information such as:

* HTTP method
* Request URL
* Request headers
* Request body
* Response body
* HTTP status code
* Response time
* Response size
* Request timestamp

Sensitive information is masked before being stored where applicable.

### Collections

Requests can be organized into collections.

A collection can contain multiple collection items, allowing related API requests to be grouped together.

### Environments

Users can create environments containing reusable variables.

Example:

```json
{
  "baseUrl": "https://api.example.com",
  "userId": "1001"
}
```

These variables can then be used in requests:

```text
{{baseUrl}}/users/{{userId}}
```

### API Documentation

The backend provides interactive API documentation using Swagger/OpenAPI.

Swagger can be used to:

* View available endpoints
* Understand request and response structures
* Test APIs
* Authorize using a JWT token

---

## 🔐 Security

Security is an important part of the application.

The backend uses:

* Spring Security
* JWT authentication
* Stateless authentication
* Password hashing
* Authorization for protected endpoints
* Input validation
* URL validation
* Sensitive-data masking
* Rate limiting
* SSRF-related URL protection

### JWT Authentication

After successful login, the server returns a JWT token.

The token can then be sent with protected requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

Spring Security validates the token before allowing access to protected endpoints.

---

## 🛡️ URL Validation

The API execution service validates request URLs before making outgoing requests.

Only:

```text
http://
https://
```

URLs are allowed.

Certain local/internal hosts are blocked to reduce the risk of server-side request forgery (SSRF).

Examples of blocked hosts include:

```text
localhost
127.0.0.1
0.0.0.0
```

---

## ⏱️ Rate Limiting

API execution requests are rate-limited to prevent excessive requests from a single user.

The current implementation uses an in-memory rate limiter.

Example configuration:

```text
10 requests / 60 seconds
```

This helps prevent accidental or excessive API execution.

---

## 📊 Monitoring

The application uses Spring Boot Actuator for application monitoring.

Available metrics include information related to:

* HTTP requests
* JVM memory
* CPU
* Application health
* API execution metrics

Prometheus is used to collect metrics.

Grafana can then be used to visualize the metrics through dashboards.

### Monitoring Architecture

```text
API Tester Backend
        |
        v
Spring Boot Actuator
        |
        v
Prometheus
        |
        v
Grafana
```

---

## 📈 Custom API Execution Metrics

The application also includes custom metrics for API executions.

These metrics help monitor:

* Total API executions
* Successful API executions
* Failed API executions

This makes it possible to understand how the API execution feature is being used and how often requests fail.

---

## 🧪 Testing

The backend contains different types of tests.

### Unit Tests

Used to test individual components in isolation.

Examples include:

* Service tests
* Utility tests
* Validation tests

### Controller Tests

Spring MVC tests using:

```text
@WebMvcTest
MockMvc
```

These tests verify controller behavior without starting the complete application.

### Integration Tests

Integration tests verify multiple parts of the application working together.

Examples include:

```text
Register
   ↓
Login
   ↓
Get JWT
   ↓
Create Environment
   ↓
Execute API
   ↓
Verify Response
```

---

## 🛠️ Technology Stack

### Backend

| Technology           | Purpose                         |
| -------------------- | ------------------------------- |
| Java 21              | Programming language            |
| Spring Boot 4        | Backend framework               |
| Spring Web           | REST APIs                       |
| Spring Data JPA      | Database access                 |
| Spring Security      | Application security            |
| JWT                  | Authentication                  |
| MySQL                | Database                        |
| Maven                | Build and dependency management |
| Swagger / OpenAPI    | API documentation               |
| Spring Boot Actuator | Monitoring                      |
| Micrometer           | Application metrics             |
| Prometheus           | Metrics collection              |
| Grafana              | Metrics visualization           |
| Docker               | Containerization                |

### Frontend

The frontend will be developed using:

* React
* JavaScript/TypeScript
* Axios
* CSS/UI framework as required

---

## 🏗️ Project Architecture

The backend follows a layered architecture.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Additional components are used for cross-cutting concerns such as:

```text
Security
Validation
Exception Handling
Metrics
Rate Limiting
Utilities
```

A simplified architecture looks like:

```text
                ┌───────────────┐
                │ React Frontend│
                └───────┬───────┘
                        │
                        │ REST API
                        ▼
              ┌───────────────────┐
              │ Spring Boot       │
              │ Backend           │
              └─────────┬─────────┘
                        │
        ┌───────────────┼────────────────┐
        │               │                │
        ▼               ▼                ▼
   Controllers      Services        Security/JWT
        │               │
        │               ▼
        │          Repositories
        │               │
        │               ▼
        │             MySQL
        │
        ▼
  API Execution
        │
        ▼
 External APIs
```

---

## 📁 Backend Project Structure

The main package structure is organized approximately as follows:

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── apitester/
    │           └── api_tester_backend/
    │               ├── controller/
    │               ├── service/
    │               ├── repository/
    │               ├── entity/
    │               ├── dto/
    │               ├── security/
    │               ├── exception/
    │               ├── util/
    │               └── config/
    │
    └── resources/
        └── application.properties
```

Tests are organized under:

```text
src/
└── test/
    └── java/
        └── com/
            └── apitester/
                └── api_tester_backend/
```

---

## 🗄️ Database

The application currently uses MySQL.

Example database:

```text
api_tester
```

The database stores application data such as:

* Users
* Environments
* Collections
* Collection items
* API execution history

---

## ⚙️ Configuration

Application configuration is kept outside the source code wherever possible.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/api_tester
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

security.jwt.secret=${JWT_SECRET}
```

Sensitive values such as database passwords and JWT secrets should **not** be committed to Git.

Environment variables can be used instead.

---

## ▶️ Running the Backend Locally

### Prerequisites

Install:

* Java 21
* Maven
* MySQL 8+
* Git

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

---

### 1. Clone the Repository

```bash
git clone <repository-url>
```

Move into the project:

```bash
cd api-tester-backend
```

---

### 2. Create the Database

Create a MySQL database:

```sql
CREATE DATABASE api_tester;
```

---

### 3. Configure Environment Variables

Configure the required database and JWT values.

For example:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

---

### 4. Run the Application

Using Maven:

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8081
```

---

## 📖 Swagger / OpenAPI

Once the application is running, Swagger UI can be used to explore and test the REST APIs.

```text
http://localhost:8081/swagger-ui/index.html
```

After logging in:

1. Register a user.
2. Login.
3. Copy the JWT token.
4. Open Swagger's **Authorize** button.
5. Enter the token using the Bearer authentication scheme.
6. Call protected endpoints.

---

## 🐳 Running with Docker

The application can also be containerized using Docker.

Build the application:

```bash
./mvnw clean package
```

Build the Docker image:

```bash
docker build -t api-tester-backend .
```

Run the container:

```bash
docker run -p 8081:8080 api-tester-backend
```

Notice the port mapping:

```text
8081:8080
│    │
│    └── Container port
└─────── Host port
```

Therefore, from the host machine the application is accessed using:

```text
http://localhost:8081
```

Inside the Docker network, other containers communicate with the backend using its container port.

---

## 📊 Monitoring with Docker

The monitoring setup can contain:

```text
API Tester Backend
       │
       ▼
   Prometheus
       │
       ▼
    Grafana
```

Prometheus can scrape the backend metrics endpoint:

```text
/actuator/prometheus
```

For example, when using Docker Compose, Prometheus can communicate with the backend using:

```text
app:8080
```

Here:

```text
app
```

is the Docker Compose service name.

And:

```text
8080
```

is the backend's **container port**, not the host port.

---

## 🔄 API Request Flow

A simplified API execution flow is:

```text
Client
  ↓
JWT Authentication
  ↓
Rate Limit Check
  ↓
Request Validation
  ↓
Environment Variable Resolution
  ↓
URL / Query / Header / Body Resolution
  ↓
Authentication Handling
  ↓
External API Request
  ↓
Response
  ↓
Save History
  ↓
Return Response to Client
```

---

## 📚 Main API Areas

The backend provides REST endpoints for areas such as:

```text
/api/auth
/api/environments
/api/api-execution
/api/history
/api/collections
/api/collection-items
```

The exact endpoints and request/response models can be explored through Swagger/OpenAPI.

---

## 🧹 Error Handling

The application uses centralized exception handling to provide consistent API error responses.

Validation errors and application-specific errors are converted into appropriate HTTP responses.

Examples include:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
500 Internal Server Error
```

---

## 🔑 HTTP Status Codes

The application follows standard HTTP status semantics.

### 400 Bad Request

The request data is invalid.

Example:

```text
Invalid URL
Missing required field
Invalid environment
```

### 401 Unauthorized

The user is not authenticated.

Example:

```text
JWT token missing
JWT token invalid
JWT token expired
```

### 403 Forbidden

The user is authenticated but does not have permission to access the requested resource.

---

## 🎯 Project Goals

The main goals of this project are:

* Build a practical REST API testing tool
* Learn Spring Boot development
* Implement secure JWT authentication
* Practice REST API design
* Work with relational databases
* Learn API request execution
* Implement request history
* Practice automated testing
* Learn Docker containerization
* Learn application monitoring
* Understand Prometheus and Grafana
* Build a React-based frontend

---

## 🗺️ Future Improvements

Planned improvements include:

* React frontend
* Request builder UI
* Collection management UI
* Environment management UI
* API history UI
* Better request/response visualization
* Import/export requests
* Improved API documentation
* Additional monitoring dashboards
* Additional automated tests
* Further security improvements

---

## 👨‍💻 Author

**Naveena P.K.**

Software Developer

This project was created as a hands-on project to learn and demonstrate modern Java backend development, REST APIs, Spring Security, testing, Docker, and application monitoring.
