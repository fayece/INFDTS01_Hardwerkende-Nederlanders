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
- A `.env` file configured in the project root, deze is meegeleverd
- pgAdmin
- IntelliJ

### Stap 1. Open het project in IntelliJ

### Stap 2. Verbind de .env file
Zorg ervoor dat Java de .env file kan uitlezen door in je launch configuratie de .env file aan te merken als "Environment variables".

### Stap 2. Start the docker containers
In de console, run:
docker compose up --build

### Stap 3.
Ga naar de juiste directory door de volgende command uit te voeren in de terminal:

cd hr-cms


### Stap 4. Creëer flyway rechten
In pgAdmin verbind met de postgres database, met de username en het wachtwoord, momenteel zijn de gegevens:
DB_NAME=DATABASE
DB_USERNAME=KENNA
DB_PASSWORD=WACHTWOORD
Connect met deze database.

Run het volgende script in de pgAdmin console:

DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'cms_superuser') THEN
    CREATE ROLE cms_superuser WITH SUPERUSER LOGIN PASSWORD 'devpassword123';
  END IF;

  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'cms_flyway') THEN
    CREATE ROLE cms_flyway WITH LOGIN CREATEROLE PASSWORD 'devpassword123';
  END IF;
END
$$;

GRANT CREATE ON DATABASE "DATABASE" TO cms_flyway;
ALTER SCHEMA public OWNER TO cms_flyway;

*de gebruikersnamen en wachtwoorden zijn afhankelijk van de .env file, maar de bovenstaande wachtwoorden komen overeen met de aangeleverde file.

### Stap 5: Start de applicatie
Start de applicatie zodat alle flyway migrations worden uitgevoerd. Hiermee worden de tabellen aangemaakt.
De applicatie draait op de port die aangegeven staat in de .env file, momenteel is dit 6031, maar dit kan aangepast worden indien nodig.

### Stap 6: Maak een admin account aan met de volgende command:
In een (Git) Bash terminal voer de volgende command uit om een admin account te maken

./make_admin.sh ronald wachtwoord

Nu kun je in de applicatie inloggen met de gebruikersnaam 'ronald' en het wachtwoord 'wachtwoord'

## Tech Stack
| **Layer**      | **Technology**                     |
|----------------|------------------------------------|
| **Language**   | Java                               |
| **Framework**  | Spring Boot                        |
| **Frontend**   | Thymeleaf                          |
| **Database**   | PostgreSQL, MongoDb, Neo4j, Redis  |
| **Migrations** | Flyway                             |
| **Build Tool** | Maven                              |
| **JDK**        | Java 25                            |
