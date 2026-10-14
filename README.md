# Clinic API (Quiz 1 + Quiz 2)

This is my Spring Boot project for a clinic. It manages patients, doctors, offices,
specializations, appointments and prescriptions.

## What I used
- Java 21
- Spring Boot 4.1 (Web MVC and Data JPA)
- PostgreSQL
- Maven

## How to run it
1. Create a database called `clinic` in PostgreSQL.
2. Check the username and password in `src/main/resources/application.properties`.
3. Run `./mvnw spring-boot:run`
4. The API runs on http://localhost:8083

I use `ddl-auto=update`, so the data stays when I restart the app, and new tables or
columns are added automatically.

## The entities
- Patient: firstName, lastName, dateOfBirth, ssNumber (unique)
- Doctor: firstName, lastName, dateOfBirth, one office, many specializations
- Office: name, officeNumber (unique)
- Specialization: name
- Appointment: appointmentDate, reason, status, patient, doctor
- Prescription (new in Quiz 2): medicineName, dosage, durationDays, issuedDate, appointment

An appointment status can be SCHEDULED, CONFIRMED, COMPLETED or CANCELLED.
The doctor owns the doctor-specialization link (table doctor_specialization).

## Rules for appointments
- A new appointment cannot be in the past, and it always starts as SCHEDULED.
- The status can only move like this:
  SCHEDULED -> CONFIRMED or CANCELLED,
  CONFIRMED -> COMPLETED or CANCELLED.
  COMPLETED and CANCELLED are final.
- The normal update (PUT) does not change the status anymore.
- A completed or cancelled appointment cannot be rescheduled.
- Prescriptions can only be written for COMPLETED appointments.
- An appointment that has prescriptions cannot be deleted.

## Quiz 2 endpoints

Part A
- PUT /api/doctors/{doctorId}/specializations/{specializationId} - add a specialization to a doctor
- DELETE /api/doctors/{doctorId}/specializations/{specializationId} - remove it from the doctor
- GET /api/offices/free - offices with no doctor
- PATCH /api/doctors/{doctorId}/office?officeNumber=104 - move a doctor to another office

Part B
- POST /api/appointments/save?patientId=...&doctorId=... - book an appointment (with the new rules)
- PATCH /api/appointments/{id}/status?status=CONFIRMED - change the status
- PATCH /api/appointments/{id}/reschedule?newDate=2026-10-30 - move to another date

Part C
- POST /api/prescriptions/save?appointmentId=... - write a prescription
- GET /api/prescriptions/appointment/{appointmentId} - prescriptions of an appointment
- GET /api/prescriptions/patient/{ssNumber} - history of a patient, newest first
- PUT /api/prescriptions/update/{id} - change medicine, dosage and days
- DELETE /api/prescriptions/delete/{id} - delete a prescription

Part D
- GET /api/doctors/{doctorId}/agenda?date=2026-10-20 - a doctor's day, without cancelled ones
- GET /api/patients/{ssNumber}/upcoming - next appointments of a patient
- GET /api/doctors/search?name=gis - search doctors by first or last name
- GET /api/dashboard/summary - numbers for the clinic

Bonus
- GET /api/prescriptions/stats/top-medicines - most prescribed medicines
- GET /api/doctors/workload?start=2026-10-01&end=2026-10-31 - completed appointments per doctor

The Quiz 1 endpoints are still there (save, all, {id}, update, delete for every entity,
and the search queries).

## Testing
I tested everything with Postman:
- `clinic.postman_collection.json` - Quiz 1
- `clinic-quiz2.postman_collection.json` - Quiz 2, one request per requirement (A1, A2 ... D4)
