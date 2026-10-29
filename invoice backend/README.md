# Smart Billing System - Backend

Spring Boot REST API for the Smart Billing System, a billing and invoice management application for small businesses.

## Features

- User registration and secure login (JWT)
- Customer management
- Product management (price and tax rate)
- Invoice creation with multiple items and status (Draft, Paid, Overdue)
- Payment tracking (Cash, UPI, Card and more)
- Dashboard with sales and payment summary
- Reports on business performance
- Company settings (company name, GST number) for the invoice header

## Tech Stack

- Java 17, Spring Boot
- Spring Security and JWT
- Spring Data JPA (Hibernate)
- PostgreSQL
- Flyway (database migrations)
- Maven

## Project Structure

controller, service, repository, entity, dto, security, db/migration

## Database Tables

users, customers, products, company_settings, invoices, invoice_items, payments, invoice_reminders

## How to Run

Requirements: Java 17, PostgreSQL

1. Create a PostgreSQL database named `invoicedb`.
2. Open `src/main/resources/application.properties` and set your own PostgreSQL username and password.
3. In the backend folder, run:
   ```
   mvnw spring-boot:run
   ```
4. The API starts on http://localhost:8081. Flyway creates the tables automatically.
