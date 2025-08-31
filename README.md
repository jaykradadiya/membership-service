# FirstClub Membership Program System

## Overview

A production-grade, enterprise-level membership program system built with Java Spring Boot, designed to handle tiered memberships with automatic upgrades, subscription management, and comprehensive business logic.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized setup)
- PostgreSQL 17.6+

### Running the Application

#### Option 1: Using Docker Compose (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd membership-program

# Start all services (database, Redis, and application)
docker-compose up -d

# The application will be available at http://localhost:8080
# Database will be available at localhost:5432
```

#### Option 2: Local Development Setup
```bash
# 1. Start PostgreSQL and Redis
docker-compose up -d db

# 2. Update application.properties with your database credentials
# 3. Run the application
mvn spring-boot:run

# 4. The application will be available at http://localhost:8080
```

#### Option 3: Using Maven
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package

# Run the JAR file
java -jar target/membership-program-*.jar
```

### Environment Configuration

#### Database Configuration
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/membership_db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

#### JWT Configuration
```properties
jwt.secret=your-jwt-secret-key
```


## 🔌 API Endpoints

### Authentication
- `POST /api/auth/login` - User login and JWT token generation

### User Management
- `POST /api/v1/users` - Create new user (Admin only)
- `GET /api/v1/users/me` - Get current user profile

### Membership Plans
- `GET /api/v1/membership/plans` - Get all available plans
  - Query param: `?includeDiscountsOnly=true` - Get only discounted plans
- `GET /api/v1/membership/plans/tier/{tierLevel}` - Get plans for specific tier

### Subscriptions
- `POST /api/v1/membership/subscribe` - Subscribe to a plan
- `GET /api/v1/membership/subscription/current` - Get current subscription
- `GET /api/v1/membership/subscription/history` - Get subscription history
- `POST /api/v1/membership/subscription/cancel` - Cancel subscription
- `POST /api/v1/membership/subscription/renew` - Renew subscription

### Tier Management
- `POST /api/v1/membership/tier/upgrade/{tierId}` - Upgrade tier
- `POST /api/v1/membership/tier/downgrade/{tierId}` - Downgrade tier

### Tier Upgrade Rules (Extensible System)
- `GET /api/v1/tier-upgrade/evaluate` - Evaluate tier upgrade eligibility for current user
- `GET /api/v1/tier-upgrade/evaluate/detailed` - Get detailed evaluation results
- `GET /api/v1/tier-upgrade/eligibility` - Check if user is eligible for upgrade
- `GET /api/v1/tier-upgrade/best-rule` - Get best applicable upgrade rule
- `GET /api/v1/tier-upgrade/applicable-rules` - Get all applicable rules
- `POST /api/v1/tier-upgrade/process-auto` - Process automatic tier upgrades
- `GET /api/v1/tier-upgrade/admin/evaluate/{userId}` - Admin: Evaluate for specific user
- `POST /api/v1/tier-upgrade/admin/process-auto/{userId}` - Admin: Process auto-upgrades for user

### Status & Information
- `GET /api/v1/membership/status` - Get user membership status

## Database Schema

### Key Tables
- `users` - User information and membership status
- `membership_plans` - Available subscription plans
- `membership_tiers` - Tier definitions and criteria
- `subscriptions` - User subscriptions
- `orders` - User order history
- `subscription_history` - Audit trail
- `tier_upgrade_rules` - Automatic upgrade configuration

### Indexes
- Performance-optimized indexes on frequently queried fields
- Composite indexes for complex queries
- Foreign key constraints for data integrity

## ✨ Features

### Core Functionality
- **User Management**: Complete user lifecycle with authentication and authorization
- **Membership Plans**: Flexible subscription plans with duration and pricing options
- **Tier System**: Multi-level membership tiers (Silver, Gold, Platinum, etc.)
- **Subscription Management**: Active subscription tracking with auto-renewal support

- **Automatic Tier Upgrades**: Rule-based system for automatic tier progression
- **Membership Benefits**: Configurable benefits per tier (delivery, discounts, access, support)
- **Scheduled Tasks**: Automated processing of expired subscriptions and tier evaluations
- **Audit Trail**: Complete history tracking for subscriptions and tier changes
- **Error Handling**: Consistent JSON error responses across all endpoints

### Business Logic
- **Criteria Evaluation**: Supports order count, monthly order value, and cohort-based rules
- **Flexible Benefits**: Configurable benefits with priority ordering and tier restrictions
- **Auto-renewal**: Intelligent subscription renewal with configurable settings
- **Performance Monitoring**: Health checks and comprehensive logging

## 🏗️ Architecture

### Technology Stack
- **Backend**: Spring Boot 3.x with Java 17
- **Database**: PostgreSQL 17.6 with JPA/Hibernate
- **Security**: Spring Security with JWT authentication
- **Scheduling**: Spring Scheduler for automated tasks
- **Validation**: Bean Validation with custom constraints

### Design Patterns
- **Repository Pattern**: Data access abstraction
- **Service Layer**: Business logic encapsulation
- **DTO Pattern**: Data transfer objects for API responses
- **Builder Pattern**: Immutable object construction
- **Strategy Pattern**: Extensible criteria evaluation system

### Key Components
- **Controllers**: REST API endpoints with proper HTTP status codes
- **Services**: Business logic implementation with transaction management
- **Repositories**: Data access layer with custom queries
- **Entities**: JPA entities with proper relationships and constraints
- **DTOs**: Request/response data transfer objects
- **Enums**: Type-safe enumeration classes
- **Exceptions**: Custom exception hierarchy with proper error codes

## 🔧 Development

### Project Structure
```
src/main/java/com/membership/program/
├── controller/          # REST API controllers
├── service/            # Business logic services
│   └── implementation/ # Service implementations
├── repository/         # Data access repositories
├── entity/            # JPA entities
├── dto/               # Data transfer objects
│   ├── request/       # Request DTOs
│   └── response/      # Response DTOs
├── enums/             # Enumeration classes
├── exception/         # Custom exceptions
├── config/            # Configuration classes
├── utility/           # Utility classes
└── constants/         # Application constants
```

### Database Migrations
- **Automatic Schema**: JPA auto-ddl for development
- **Seed Data**: SQL scripts for initial data population
- **Indexes**: Performance-optimized database indexes
- **Constraints**: Foreign key and validation constraints


## 📊 Monitoring & Health

### Health Checks
- **Application Health**: Spring Boot Actuator health endpoint
- **Database Health**: Connection pool and query performance monitoring

### Scheduled Tasks
- **Tier Evaluation**: Daily at 2 AM - Automatic tier upgrade processing
- **Subscription Processing**: Daily at 3 AM - Expired subscription handling
- **Data Cleanup**: Periodic cleanup of old audit records
- **Performance Optimization**: Regular database index maintenance
