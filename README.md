# JWT Authentication Project

This project provides a secure JWT-based authentication system built with Spring Boot. It handles user registration, login, password reset, and features email notifications.  Accounts are activated using secure email validation codes.

## Features

- User Registration: New user registration with email and password.
- Login: User login with email and password, JWT token generation.
- Password Reset: Password reset functionality via email.
- JWT Authorization: Secure access to protected endpoints using JWT.
- API Documentation: Integrated Swagger for easy API exploration.
- Email Notifications: Email notifications for user registration and password reset.
- Docker Support: Easy containerization and deployment with Docker.

## Technologies

- Java, Spring Boot, Spring Security
- Spring Data JPA, PostgreSQL
- JWT (JSON Web Token)
- Lombok, ModelMapper, Flyway
- Springdoc OpenAPI (Swagger)
- Docker
- JavaMailSender

## Setup

### Requirements

- Java 17+
- Maven/Gradle
- PostgreSQL
- Docker (optional)
- Email server (e.g., Gmail, Mailtrap)

### Local Installation

1. Clone the repository: `git clone https://github.com/kubilaybee/JwtAuthProject.git`
2. Open in your IDE (IntelliJ IDEA, Eclipse).
3. Install dependencies: `mvn install` (Maven) or `gradle build` (Gradle).
4. Configure database and email settings in `application.properties` or `application.yml`.
5. Run the application.

### Docker Installation

1. Clone the repository.
2. Build the Docker image: `docker build -t jwt-auth-project .`
3. Run PostgreSQL Docker container (if needed): `docker run --name postgres-db -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres`
4. Run the application container (link to database and configure email via environment variables):

```bash
docker run -p 8080:8080 --link postgres-db \
-e SPRING_DATASOURCE_URL="jdbc:postgresql://postgres-db:5432/your_db_name" \
-e SPRING_DATASOURCE_USERNAME="user" \
-e SPRING_DATASOURCE_PASSWORD="password" \
-e SPRING_MAIL_HOST="mail.example.com" \
-e SPRING_MAIL_PORT="587" \
-e SPRING_MAIL_USERNAME="user@example.com" \
-e SPRING_MAIL_PASSWORD="password" \
jwt-auth-project
