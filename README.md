# 💰 Expense Tracker API

## 📑 Table of Contents
1. [📄 Project Summary](#-project-summary)
2. [🖥️ Languages & Tools](#%EF%B8%8F-languages--tools)
3. [⚙️ Methodologies & Features](#%EF%B8%8F-methodologies--features)
4. [🔄 Planned Features in Further Development](#-planned-features-in-further-development)

---

## 📄 Project Summary

**High-Level Overview:**  
Expense Tracker API is a **multi-user expense management system** built to record, organize, and manage personal expenses with **secure authentication, auditing, and soft delete functionality**.  
It’s designed as a **scalable backend** that will be extended with a **React.js frontend** and deployed to the cloud for production use.

**Technical Summary:**  
Developed using **Java 17, Spring Boot, and PostgreSQL**, with **Liquibase** for database version control, this API follows a modular and maintainable architecture.  
Security is ensured through **JWT-based authentication** and password hashing, while auditing captures all create/modify operations.  
The design supports clean separation of concerns with **DTOs, Mappers, and layered services**.

---

## 🖥️ Languages & Tools

**Languages:**
- Java 17  
- SQL (PostgreSQL dialect)

**Frameworks & Libraries:**
- Spring Boot 3 (Web, Data JPA, Security)  
- Liquibase (Database migrations)  
- JWT (Authentication)  
- Maven (Build tool)

**Database:**
- PostgreSQL

**Development Tools:**
- IntelliJ IDEA  
- pgAdmin  
- Git + GitHub for version control

---

## ⚙️ Methodologies & Features

- **RESTful API Design** with proper HTTP status codes  
- **JWT Authentication & Authorization** (login via username or email)  
- **Soft Delete** with `active` flag for both users and expenses  
- **Auditing**: Tracks `created_by`, `created_date`, `modified_by`, `modified_date`  
- **Entity Relationships**: One-to-many relationship between `User` and `Expense`  
- **Enum-based Category Management** for structured expense categorization  
- **Cascading Deletes**: Removing a user deactivates their expenses  
- **Validation**: Strict password policy, required fields for key attributes  
- **DTOs & Mappers** for clean separation of entity and API payloads  

---

## 🔄 Planned Features in Further Development

- **Frontend Integration:**  
  - React.js + JavaScript frontend consuming the API  
  - Real-time form validation (username/email uniqueness, password strength)  
  - Responsive design with Material UI  

- **Cloud Deployment:**  
  - Deploy to Azure or AWS with containerization (Docker)  
  - CI/CD pipeline with GitHub Actions  

- **Advanced Search & Analytics:**  
  - Integration with ElasticSearch for full-text search  
  - Expense analytics dashboard with charts and summaries  

- **Additional Features:**  
  - Pagination and sorting for expense listings  
  - Multi-currency support  
  - Export data to CSV/PDF  
