package kigali.clinic.rw.controller;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.service.AppointmentService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.service.PrescriptionService;

@RestController
@RequestMapping(value = "/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService presServe;

    @Autowired
    private AppointmentService appServe;


    @PostMapping(value = "/save")
    public ResponseEntity<?> savePrescription(@RequestBody Prescription prescription,
            @RequestParam UUID appointmentId) {

        String returnedMessage = presServe.savePrescription(prescription, appointmentId);

        if (returnedMessage.equals("Prescription saved successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
        } else if (returnedMessage.equals("The appointment with that id does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(returnedMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/appointment/{appointmentId}")
    public ResponseEntity<?> getPrescriptionsOfAppointment(@PathVariable UUID appointmentId){

        Optional<Appointment> appointment = appServe.getAppointmentById(appointmentId);
        if(appointment.isEmpty()){
            return new ResponseEntity<>("The appointment with that id does not exist", HttpStatus.NOT_FOUND);
        }

        List<Prescription> prescriptions = presServe.getPrescriptionsOfAppointment(appointment.get());
        return new ResponseEntity<>(prescriptions, HttpStatus.OK);
    }


}
