# Ticker
# Secure Multi-Tenant SaaS Backend (Spring Boot)

A production-grade backend system built using **Spring Boot** that enables multiple organizations (tenants) to securely share a single application instance while maintaining **strict tenant-level data isolation**, **robust authentication**, and **role-based authorization**.

This project is designed to demonstrate **enterprise backend engineering skills** expected by **product-based companies**.

---

## 🚀 Key Highlights

* Multi-tenant architecture with tenant-aware data access
* JWT-based authentication with refresh token support
* Role-Based Access Control (RBAC)
* Secure, DTO-driven REST APIs
* Auditing and compliance-friendly activity tracking
* Clean layered architecture following Spring Boot best practices

---

## 🏗️ Architecture Overview

```
Client
  ↓
AuthController / API Controllers (DTO layer)
  ↓
Service Layer (Business logic, security rules)
  ↓
Repository Layer (Tenant-aware JPA queries)
  ↓
PostgreSQL Database (Shared schema, tenant-isolated rows)
```

* **Controllers** handle HTTP concerns only
* **Services** enforce authentication, authorization, and business rules
* **Repositories** guarantee tenant isolation at the data access level

---

## 🧩 Multi-Tenancy Model

### Tenant Strategy

* **Shared Database, Shared Schema**
* Each record contains a `tenant_id`
* Tenant identity is propagated via **JWT claims**

### Tenant Identification Flow

1. Tenant resolved during login (header or email-to-tenant mapping)
2. Tenant ID embedded into JWT
3. Tenant extracted from JWT for all subsequent requests
4. Repository queries filtered using tenant context

This ensures **zero cross-tenant data leakage**.

---

## 🔐 Authentication & Authorization

### Authentication

* Username/password login
* Passwords hashed using **BCrypt**
* Short-lived **access tokens** (JWT)
* Long-lived **refresh tokens** stored and revocable

### JWT Claims Structure

```json
{
  "sub": "userId",
  "tenantId": "acme",
  "roles": ["TENANT_ADMIN"],
  "iat": 1710000000,
  "exp": 1710003600
}
```

### Authorization

* Role hierarchy: `SUPER_ADMIN → TENANT_ADMIN → USER`
* Enforced using Spring Security annotations and tenant checks

---

## 📦 API Design

### Auth APIs

```
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
POST /api/auth/register
```

* APIs are **stateless**
* DTO-based contracts to avoid entity exposure

---

## 🧱 DTO Strategy

* Entities represent persistence models
* DTOs define API contracts
* Prevents leakage of internal fields (passwords, tenant IDs)
* Enables API evolution without DB coupling

## 🛡️ Security Best Practices

* BCrypt password hashing
* Short-lived access tokens
* Refresh token revocation
* Centralized exception handling
* Input validation using `@Valid`
* Tenant-level authorization checks

## 🛠️ Tech Stack

* **Language:** Java
* **Framework:** Spring Boot
* **Security:** Spring Security, JWT, BCrypt
* **Persistence:** JPA / Hibernate
* **Database:** PostgreSQL
* **Build Tool:** Maven
* **Deployment:** Docker (optional)

## 📌 Future Enhancements

* Enhancing ticker features to create/modify tickers. 
* Using AI to analyse ticker data and provide insights.
* Schema-per-tenant support
* Rate limiting per tenant
* Redis caching
* Event-driven auditing
* OAuth2 / SSO integration
