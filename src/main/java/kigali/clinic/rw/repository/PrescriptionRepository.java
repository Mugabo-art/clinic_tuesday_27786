package kigali.clinic.rw.repository;

import java.util.UUID;
import java.util.List;

import kigali.clinic.rw.domain.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    List<Prescription> findByAppointment(Appointment appointment);
}
