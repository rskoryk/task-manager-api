# Task Manager API

A RESTful API for task management with JWT authentication, email verification, pagination, sorting, and role-based access control.

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Security 6 + JWT
- PostgreSQL
- Docker + Docker Compose
- Swagger / OpenAPI
- JUnit 5 + Mockito

## Features

- User registration with email verification
- JWT authentication
- Role-based access control (USER, ADMIN)
- CRUD operations for tasks
- Tasks are bound to authenticated user
- Pagination and sorting
- Admin panel (manage users, view any user's tasks)
- User can update and delete their own account

## Getting Started

### Prerequisites

- Docker + Docker Compose

### Run with Docker

1. Clone the repository:
```bash
   git clone https://github.com/rskoryk/task-manager-api.git
   cd task-manager-api
```

2. Create `.env` file from the example:
```bash
   cp .env.example .env
```

3. Fill in the `.env` file with your values:
```bash
   JWT_SECRET=your_base64_secret
   MAIL_USERNAME=your_mailtrap_username
   MAIL_PASSWORD=your_mailtrap_password
```
4. Run the application:
```bash
   docker-compose up --build
```

5. Open Swagger UI:
```bash
   http://localhost:8080/swagger-ui/index.html
```

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register a new user |
| POST | /api/auth/login | Login and get JWT token |
| GET | /api/auth/verify | Verify email |

### Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/tasks | Create a task |
| GET | /api/tasks | Get all tasks (paginated) |
| GET | /api/tasks/{id} | Get task by ID |
| GET | /api/tasks/by-title/{title} | Get task by title |
| GET | /api/tasks/status/{status} | Get tasks by status |
| PUT | /api/tasks/{id} | Update task |
| DELETE | /api/tasks/{id} | Delete task |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/users/me | Get current user |
| PUT | /api/users/me | Update current user |
| DELETE | /api/users/me | Delete current user |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/users | Get all users (paginated) |
| GET | /api/admin/users/{id} | Get user by ID |
| GET | /api/admin/users/{id}/tasks | Get user's tasks |
| PATCH | /api/admin/users/{id}/toggle | Toggle user enabled/disabled |
| DELETE | /api/admin/users/{id} | Delete user |

## Running Tests

```bash
  ./mvnw test
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| JWT_SECRET | Base64 encoded secret key (min 256 bit) |
| MAIL_USERNAME | Mailtrap SMTP username |
| MAIL_PASSWORD | Mailtrap SMTP password |
