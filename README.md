# Notifications Service

Minimal Spring Boot service for HayDay notifications. Implements the stable API contract expected by the React UI.

## Run

### Prerequisites
- Java 17+
- MongoDB running locally (optional for development)

### Start

```bash
# Using Maven from tools folder
e:\JavaSpringboot\tools\apache-maven-3.9.11\bin\mvn.cmd spring-boot:run

# Or if Maven is on PATH
mvn spring-boot:run
```

The service runs on **port 8080**.

## Docker Compose

The service can be started with Docker Compose after you provide a MongoDB Atlas connection string in a local `.env` file.

```bash
copy .env.example .env
# edit .env and replace MONGODB_URI with your Atlas connection string
docker compose up --build
```

Compose runs a short Atlas ping container first. The notification service starts only if that ping succeeds.

## API

All endpoints return JSON in the format expected by the React UI.

### List Notifications
```
GET /api/notifications
Header: X-User-Id: user-1 (optional, defaults to user-1)
```

Response:
```json
{
  "success": true,
  "data": [ ... ],
  "meta": { "total_count": 2, "unread_count": 1, "attention_count": 1, "limit": 25 }
}
```

### Create Notification
```
POST /api/notifications
Content-Type: application/json
```

Request:
```json
{
  "user_id": "user-1",
  "category": "activity",
  "level": "success",
  "title": "Animal Fed",
  "message": "You fed the cow.",
  "action_url": "/farm/animals/1",
  "animal_id": "animal-1",
  "metadata": { "source": "user-action" },
  "dedup_key": "cow-fed-2026-05-05"
}
```

### Mark Single Notification as Read
```
PATCH /api/notifications/{id}/read
```

### Mark All Notifications as Read
```
PATCH /api/notifications/read-all
Header: X-User-Id: user-1 (optional)
```

### Resolve Attention Notification
```
PATCH /api/notifications/{id}/resolve
```

### Delete Notification
```
DELETE /api/notifications/{id}
```

## MongoDB

If MongoDB is running locally on port 27017, notifications are persisted to the `hayday` database.

If MongoDB is not available, the app will fail to start unless you disable auto-index creation or switch to an in-memory profile.

### Setup MongoDB (optional)

```powershell
# Using Windows Subsystem for Linux or Docker
docker run -d -p 27017:27017 mongo:latest
```

Then the service will auto-create the `notifications` collection.

## Testing

```bash
e:\JavaSpringboot\tools\apache-maven-3.9.11\bin\mvn.cmd test
```

## Key Design Decisions

- **API Contract**: Matches the React UI expectations exactly. If you swap the backend to Laravel, change the controller implementation but keep the response shape identical.
- **MongoDB**: Persists notifications by default. Seed data is provided on first run.
- **User ID**: Passed via `X-User-Id` header. Defaults to `user-1` for testing.
- **De-duplication**: Use `dedup_key` to prevent duplicate recurring alerts.

