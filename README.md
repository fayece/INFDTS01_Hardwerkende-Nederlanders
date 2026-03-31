# Spotlight! 
### A Content Management System by Team Hardwerkende Nederlanders built with Java
- Faye Elsinga (1079070)
- Kenna Ndir (1061885)
- Rein Schregardus (1079189)

---

## Introduction
Spotlight! is an intranet CMS with a forum. It is designed specifically for venues to post and share information. The CMS allows users to create and manage content, as well as engage in discussions through the forum.

---

## How to run
### Prerequisites
- [Docker](https://www.docker.com/) installed and running.
- A `.env` file configured in the project root ( see `.env.example`)

### Step 1. Navigate to the project directory
```bash
cd hr-cms
```

### Step 2. Build the project
If you have Maven installed:
```bash
mvn clean install
```

If you don't have Maven installed, use the wrapper.

MacOS/Linux:
```bash
./mvnw clean install
```
Windows:
```bash
mvnw.cmd clean install
```

> Troubleshooting: Ensure you have **Java 25** installed. Run `java -version` to check.

### Step 3. Start the application
```bash
docker compose --env-file ../.env -f ../compose.yml -p hard-work up -d --build
```

After the application is up and running, it can be accessed on localhost, using the port specified in the .env file.

---

## Tech Stack
| **Layer**      | **Technology** |
|----------------|----------------|
| **Language**   | Java           |
| **Framework**  | Spring Boot    |
| **Frontend**   | Thymeleaf      |
| **Database**   | PostgreSQL     |
| **Migrations** | Flyway         |
| **Build Tool** | Maven          |
| **JDK**        | Java 25        |

