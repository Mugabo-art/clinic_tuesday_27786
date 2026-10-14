package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;

import kigali.clinic.rw.domain.AppointmentStatus;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,UUID> {

    Optional<Doctor> findByOfficeId(UUID officeId);

    @Query("SELECT d FROM Doctor d JOIN d.specializations s " +
           "WHERE LOWER(s.name) = LOWER(:name)")
    List<Doctor> findBySpecializationName(@Param("name") String name);

    @Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.lastName ASC")
    List<Doctor> findWithoutOffice();
    boolean existsByOfficeOfficeNumberAndIdNot(int officeNumber, UUID doctorId);

    @Query("SELECT d FROM Doctor d " +
           "WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "ORDER BY d.lastName ASC")
    List<Doctor> searchByName(@Param("name") String name);

    @Query("SELECT d.firstName, d.lastName, COUNT(a) " +
           "FROM Appointment a JOIN a.doctor d " +
           "WHERE a.status = :status AND a.appointmentDate BETWEEN :start AND :end " +
           "GROUP BY d.id, d.firstName, d.lastName " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> countWorkload(@Param("status") AppointmentStatus status, @Param("start") LocalDate start, @Param("end") LocalDate end);

}
