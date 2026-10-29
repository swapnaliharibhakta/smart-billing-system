# Smart Billing System

A personal project built to practice full stack development.

A full-stack billing and invoice management web application for small businesses. It replaces paper and Excel bills with a simple digital system.

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

Backend
- Java 17, Spring Boot
- Spring Security and JWT
- Spring Data JPA (Hibernate)
- PostgreSQL
- Flyway (database migrations)
- Maven

Frontend
- React 19 and Vite
- Redux Toolkit
- Material UI (MUI)
- Axios
- Recharts

## Project Structure

- `invoice backend` - Spring Boot REST API (runs on port 8081)
  - controller, service, repository, entity, dto, security, db/migration
- `invoice frontend` - React application
  - pages, components, api, store

## Database Tables

users, customers, products, company_settings, invoices, invoice_items, payments, invoice_reminders

## How to Run

Requirements: Java 17, PostgreSQL, Node.js

Backend
1. Create a PostgreSQL database named `invoicedb`.
2. Open `invoice backend/src/main/resources/application.properties` and set your own PostgreSQL username and password.
3. In the `invoice backend` folder, run:
   ```
   mvnw spring-boot:run
   ```
4. The API starts on http://localhost:8081. Flyway creates the tables automatically.

Frontend
1. In the `invoice frontend` folder, run:
   ```
   npm install
   npm run dev
   ```
2. Open the link shown in the terminal (usually http://localhost:5173).
