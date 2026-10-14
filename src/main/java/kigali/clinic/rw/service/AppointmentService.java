package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;


@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appRepo;

    @Autowired
    private PatientRepository patRepo;

    @Autowired
    private PrescriptionRepository presRepo;

    @Autowired
    private DoctorRepository docRepo;

    public String saveAppointment(Appointment appointment, UUID patientId, UUID doctorId) {

        Optional<Patient> patient = patRepo.findById(patientId);
        if (patient.isEmpty()) {
            return "Patient not found";
        }

        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if (doctor.isEmpty()) {
            return "Doctor not found";
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        boolean alreadyBooked = appRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(doctorId,
                appointment.getAppointmentDate(), AppointmentStatus.CANCELLED);
        if (alreadyBooked) {
            return "Doctor is already booked on that date";
        }

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        appointment.setPatient(patient.get());
        appointment.setDoctor(doctor.get());
        appRepo.save(appointment);
        return "Appointment is saved successfully";
    }

    public List<Appointment> getAllAppointments() {
        return appRepo.findAll();
    }

    public Optional<Appointment> getAppointmentById(UUID id) {
        return appRepo.findById(id);
    }

    public String updateAppointment(UUID id, Appointment newAppointment) {

        Optional<Appointment> existing = appRepo.findById(id);
        if (existing.isEmpty()) {
            return "Appointment not found";
        }

        Appointment appointment = existing.get();
        appointment.setAppointmentDate(newAppointment.getAppointmentDate());
        appointment.setReason(newAppointment.getReason());
        appRepo.save(appointment);
        return "Appointment is updated successfully";
    }

    public String deleteAppointment(UUID id) {

        if (!appRepo.existsById(id)) {
            return "Appointment not found";
        }

        if (presRepo.existsByAppointmentId(id)) {
            return "That appointment has prescriptions, delete them first";
        }

        appRepo.deleteById(id);
        return "Appointment is deleted successfully";
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getAppointmentsBetween(LocalDate start, LocalDate end) {
        return appRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> countAppointmentsPerStatus() {
        return appRepo.countAppointmentsPerStatus();
    }

    @Transactional
    public String cancelDay(UUID doctorId, LocalDate date) {
        int cancelled = appRepo.cancelDay(doctorId, date, AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
        return cancelled + " appointments cancelled";
    }

    public Page<Appointment> getAppointmentsPage(Pageable pageable) {
        return appRepo.findAll(pageable);
    }

    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int deleted = appRepo.deleteByStatusBefore(AppointmentStatus.CANCELLED, date);
        return deleted + " appointments deleted";
    }

    public String changeStatus(UUID id, AppointmentStatus newStatus){

        Optional<Appointment> existing = appRepo.findById(id);
        if(existing.isEmpty()){
            return "The appointment with that id does not exist";
        }

        Appointment appointment = existing.get();
        AppointmentStatus currentStatus = appointment.getStatus();

        if(!isAllowedMove(currentStatus, newStatus)){
            return "Cannot change status from " + currentStatus + " to " + newStatus;
        }

        appointment.setStatus(newStatus);
        appRepo.save(appointment);
        return "Appointment status updated successfully";
    }

    public String reschedule(UUID id, LocalDate newDate){

        Optional<Appointment> existing = appRepo.findById(id);
        if(existing.isEmpty()){
            return "The appointment with that id does not exist";
        }

        Appointment appointment = existing.get();

        if(appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.CANCELLED){
            return "A " + appointment.getStatus() + " appointment cannot be rescheduled";
        }

        if(newDate.isBefore(LocalDate.now())){
            return "Appointment date cannot be in the past";
        }

        UUID doctorId = appointment.getDoctor().getId();
        boolean alreadyBooked = appRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(doctorId, newDate, AppointmentStatus.CANCELLED);
        if(alreadyBooked){
            return "Doctor is already booked on that date";
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appRepo.save(appointment);
        return "Appointment rescheduled successfully";
    }


    private boolean isAllowedMove(AppointmentStatus current, AppointmentStatus next){

        if(current == AppointmentStatus.SCHEDULED){
            if(next == AppointmentStatus.CONFIRMED || next == AppointmentStatus.CANCELLED){
                return true;
            }
        }

        if(current == AppointmentStatus.CONFIRMED){
            if(next == AppointmentStatus.COMPLETED || next == AppointmentStatus.CANCELLED){
                return true;
            }
        }

        return false;
    }

}
