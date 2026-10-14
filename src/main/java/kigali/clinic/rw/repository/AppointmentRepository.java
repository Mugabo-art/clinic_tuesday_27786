package kigali.clinic.rw.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

       List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

       List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);

       List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate appointmentDate,
                     AppointmentStatus status);

       List<Appointment> findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                     String ssNumber, LocalDate date, List<AppointmentStatus> statuses);

       boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate appointmentDate,
                     AppointmentStatus status);

       @Query("SELECT a.status, COUNT(a) FROM Appointment a " +
                     "GROUP BY a.status ORDER BY COUNT(a) DESC")
       List<Object[]> countAppointmentsPerStatus();

       @Modifying
       @Query("UPDATE Appointment a SET a.status = :newStatus " +
                     "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> :skippedStatus")
       int cancelDay(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date,
                     @Param("newStatus") AppointmentStatus newStatus,
                     @Param("skippedStatus") AppointmentStatus skippedStatus);

       @Modifying
       @Query("DELETE FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
       int deleteByStatusBefore(@Param("status") AppointmentStatus status, @Param("date") LocalDate date);
       
       long countByAppointmentDate(LocalDate appointmentDate);

       long countByStatus(AppointmentStatus status);

}

