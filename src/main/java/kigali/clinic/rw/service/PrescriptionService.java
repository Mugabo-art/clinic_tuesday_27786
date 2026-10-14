package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository presRepo;

    @Autowired
    private AppointmentRepository appRepo;

    public String savePrescription(Prescription prescription, UUID appointmentId) {

        Optional<Appointment> appointment = appRepo.findById(appointmentId);
        if (appointment.isEmpty()) {
            return "The appointment with that id does not exist";
        }

        if (appointment.get().getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }

        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment.get());
        presRepo.save(prescription);
        return "Prescription saved successfully";
    }

    public List<Prescription> getPrescriptionsOfAppointment(Appointment appointment) {
        return presRepo.findByAppointment(appointment);
    }

    public List<Prescription> getHistoryOfPatient(String ssNumber){
        return presRepo.findHistoryOfPatient(ssNumber);
    }

    public String updatePrescription(UUID id, Prescription newPrescription){

        Optional<Prescription> existing = presRepo.findById(id);
        if(existing.isEmpty()){
            return "The prescription with that id does not exist";
        }

        Prescription prescription = existing.get();
        prescription.setMedicineName(newPrescription.getMedicineName());
        prescription.setDosage(newPrescription.getDosage());
        prescription.setDurationDays(newPrescription.getDurationDays());
        presRepo.save(prescription);
        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id){

        if(!presRepo.existsById(id)){
            return "The prescription with that id does not exist";
        }

        presRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

}
