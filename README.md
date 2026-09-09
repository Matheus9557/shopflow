# ShopFlow — E-commerce Backend API

ShopFlow is a backend system for an e-commerce platform built with **Java 21 and Spring Boot**, designed to evolve from a modular monolithic architecture into a scalable, production-oriented backend.

The project focuses on applying real-world backend engineering practices, including RESTful API design, relational persistence, caching, validation, security, asynchronous communication, automated testing, and scalable architectural patterns.

---

## 🚀 Tech Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* PostgreSQL
* Redis
* Maven
* Docker
* Git & GitHub

### Planned Technologies

The project will progressively incorporate:

* Spring Security
* JWT / OAuth2
* RabbitMQ
* JUnit 5
* Mockito
* Testcontainers
* Docker Compose
* Angular

---

## 🧱 Architecture

The project currently follows a **layered architecture** with clear separation of responsibilities:

* **Controller Layer** → Handles HTTP requests and responses
* **Service Layer** → Implements application and business logic
* **Repository Layer** → Handles data access and persistence
* **Entity Layer** → Represents domain models mapped with JPA
* **DTO Layer** → Defines API request and response contracts
* **Configuration Layer** → Centralizes infrastructure and framework configuration

The architecture is intentionally designed to evolve as new requirements are introduced, including authentication, asynchronous messaging, domain-driven design, and distributed system patterns.

---

## ⚙️ Current Features

### Product Module

* Product CRUD
* Product request validation with Jakarta Bean Validation
* PostgreSQL persistence
* Transaction management
* Redis caching
* Cache-Aside pattern
* Redis cache update after product modification
* Redis cache eviction after product deletion
* Configurable cache TTL
* Typed Redis serialization with Jackson
* `LocalDateTime` serialization support

### Testing

* Unit tests with JUnit 5
* Mockito-based service isolation
* Product service test coverage for:

    * Product creation
    * Cache miss
    * Cache hit
    * Product not found
    * Product update
    * Cache update
    * Product deletion
    * Cache eviction

---

## 🧠 Key Engineering Concepts

* Clean Code
* SOLID principles
* Object-Oriented Programming (OOP)
* Separation of Concerns
* Dependency Injection
* REST API design
* DTO pattern
* Layered architecture
* Transaction management
* Cache-Aside pattern
* Cache invalidation
* Input validation
* Unit testing
* Relational data modeling

---

## 🗄️ Database

The project uses **PostgreSQL** as its primary relational database.

Persistence is implemented using:

* Spring Data JPA
* Hibernate
* JPA entity mapping
* Transaction management
* Database-generated identifiers
* Relational constraints

---

## ⚡ Caching

Redis is used as a distributed cache for frequently accessed product data.

The Product module currently implements the **Cache-Aside pattern**:

```text
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ├──► Redis ──► Cache HIT ──► Response
   │
   └──► Cache MISS
            │
            ▼
        PostgreSQL
            │
            ▼
          Redis
            │
            ▼
         Response
```

Product updates refresh the corresponding cache entry, while product deletion removes the cached representation.

---

## 🧪 Testing

The project uses **JUnit 5 and Mockito** for automated unit testing.

Current Product Service tests validate:

* Successful product creation
* Redis cache miss and database fallback
* Redis cache hit
* Not-found scenarios
* Product updates
* Cache synchronization after updates
* Product deletion
* Cache eviction

Current test status:

```text
Tests run: 7
Failures: 0
Errors: 0
Skipped: 0
```

---

## 📦 Project Structure

```text
src/
├── main/
│   └── java/
│       └── com/matheus/shopflow/
│           ├── config/
│           ├── product/
│           │   ├── controller/
│           │   ├── dto/
│           │   ├── entity/
│           │   ├── repository/
│           │   └── service/
│           └── shared/
│               ├── exception/
│               └── model/
│
└── test/
    └── java/
        └── com/matheus/shopflow/
            └── product/
                └── service/
```

---

## 🛣️ Development Roadmap

The project is being developed incrementally, with each stage introducing additional backend engineering concepts.

* [x] Product CRUD
* [x] Bean Validation
* [x] Redis Cache-Aside
* [x] Redis cache update
* [x] Redis cache eviction
* [x] Product service unit tests
* [ ] Authentication and authorization
* [ ] Spring Security
* [ ] JWT / OAuth2
* [ ] RabbitMQ
* [ ] Asynchronous processing
* [ ] Distributed system patterns
* [ ] Domain-Driven Design
* [ ] Hexagonal Architecture
* [ ] CQRS
* [ ] Microservices
* [ ] Angular frontend

---

## 🎯 Project Goal

ShopFlow is being developed as a **real-world backend engineering project**, with the goal of progressively applying architecture and infrastructure patterns commonly found in modern production systems.

Rather than implementing all patterns at once, the project evolves incrementally so that each architectural decision is supported by an actual business requirement.

The long-term goal is to build a complete e-commerce platform demonstrating:

* Robust REST APIs
* Secure authentication and authorization
* Reliable data persistence
* Distributed caching
* Asynchronous communication
* Automated testing
* Domain-driven design
* Scalable architecture
* Observability
* Containerized deployment
* Distributed system patterns

---

## 👨‍💻 Author

**Matheus Gomes Pinto**

Backend / Full Stack Software Developer

* Java
* Spring Boot
* Node.js
* React
* PostgreSQL
* Redis
* Docker
* REST APIs
