package kigali.clinic.rw.service;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

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

    public String savePrescription(Prescription prescription, UUID appointmentId){

        Optional<Appointment> appointment = appRepo.findById(appointmentId);
        if(appointment.isEmpty()){
            return "The appointment with that id does not exist";
        }

        if(appointment.get().getStatus() != AppointmentStatus.COMPLETED){
            return "Prescriptions are only for COMPLETED appointments";
        }

        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment.get());
        presRepo.save(prescription);
        return "Prescription saved successfully";
    }


}
