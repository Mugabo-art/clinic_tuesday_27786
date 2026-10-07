# Clinic Management API

A Spring Boot REST API for managing a clinic: doctors, patients, offices, specializations and appointments.

## Tech stack

- Java 21
- Spring Boot 4.1 (Web MVC, Data JPA)
- PostgreSQL
- Maven

## Domain model

| Entity | Fields | Relationships |
|---|---|---|
| Doctor | firstName, lastName, dateOfBirth | One-to-one with Office, many-to-many with Specialization, one-to-many with Appointment |
| Patient | firstName, lastName, dateOfBirth | One-to-many with Appointment |
| Office | name, officeNumber | One-to-one with Doctor |
| Specialization | name | Many-to-many with Doctor |
| Appointment | appointmentDate, reason, status | Many-to-one with Patient and Doctor |

Appointment status is one of `SCHEDULED`, `CONFIRMED`, `COMPLETED`, `CANCELLED`. All ids are UUIDs.

## Running the project

1. Create a PostgreSQL database named `clinic`.
2. Update the username and password in `src/main/resources/application.properties` if they differ from yours.
3. Start the app:

   ```
   ./mvnw spring-boot:run
   ```

The API runs on `http://localhost:8083`. Tables are recreated on every start (`ddl-auto=create`), so data does not persist between runs.

## API endpoints

Every resource has the same CRUD endpoints under its base path:

| Method | Path | Description |
|---|---|---|
| POST | `/save` | Create |
| GET | `/all` | List all |
| GET | `/{id}` | Get one by id |
| PUT | `/update/{id}` | Update |
| DELETE | `/delete/{id}` | Delete |

Base paths: `/api/doctors`, `/api/patients`, `/api/offices`, `/api/specializations`, `/api/appointments`.

### Extra queries

| Method | Path | Parameters |
|---|---|---|
| GET | `/api/doctors/by-specialization` | `name` |
| GET | `/api/doctors/without-office` | |
| GET | `/api/patients/by-last-name` | `lastName` |
| GET | `/api/patients/of-doctor/{doctorId}` | |
| GET | `/api/patients/frequent` | `min` (minimum number of appointments) |
| GET | `/api/offices/busiest` | |
| GET | `/api/specializations/unused` | |
| POST | `/api/specializations/{id}/doctor/{doctorId}` | Assign a doctor to a specialization |
| DELETE | `/api/specializations/{id}/doctor/{doctorId}` | Remove a doctor from a specialization |
| GET | `/api/appointments/by-status` | `status` |
| GET | `/api/appointments/between` | `start`, `end` (dates) |
| GET | `/api/appointments/stats/by-status` | |
| PATCH | `/api/appointments/cancel-day` | `doctorId`, `date` |
| GET | `/api/appointments/page` | `page`, `size`, `sort` |
| DELETE | `/api/appointments/cancelled-before` | `date` |

## Testing

Import `clinic.postman_collection.json` into Postman to get ready-made requests for every endpoint.
