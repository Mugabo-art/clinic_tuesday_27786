package kigali.clinic.rw.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

import kigali.clinic.rw.domain.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    List<Prescription> findByAppointment(Appointment appointment);

    @Query("SELECT p FROM Prescription p " +
            "WHERE p.appointment.patient.ssNumber = :ss " +
            "ORDER BY p.issuedDate DESC")
    List<Prescription> findHistoryOfPatient(@Param("ss") String ssNumber);

    boolean existsByAppointmentId(UUID appointmentId);

}
