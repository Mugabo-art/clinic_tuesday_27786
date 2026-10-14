package kigali.clinic.rw.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.DoctorService;
import kigali.clinic.rw.service.PatientService;


@RestController
@RequestMapping (value="/api/patients")
public class PatientController {

    @Autowired
    private PatientService patServe;

    @Autowired
    private DoctorService docServe;


    @PostMapping(value = "/save")
    public ResponseEntity<?> savePatient(@RequestBody Patient patient){
        String returnedMessage = patServe.savePatient(patient);
        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllPatients(){
        List<Patient> patients = patServe.getAllPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable UUID id){

        Optional<Patient> patient = patServe.getPatientById(id);

        if(patient.isPresent()){
            return new ResponseEntity<>(patient.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable UUID id, @RequestBody Patient patient){

        String returnedMessage = patServe.updatePatient(id, patient);

        if(returnedMessage.equals("Patient is updated successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable UUID id){

        String returnedMessage = patServe.deletePatient(id);

        if(returnedMessage.equals("Patient is deleted successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("Patient not found")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/by-last-name")
    public ResponseEntity<?> getPatientsByLastName(@RequestParam String lastName){
        List<Patient> patients = patServe.getPatientsByLastName(lastName);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping(value = "/of-doctor/{doctorId}")
    public ResponseEntity<?> getPatientsOfDoctor(@PathVariable UUID doctorId){

        if(docServe.getDoctorById(doctorId).isEmpty()){
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        }

        List<Patient> patients = patServe.getPatientsOfDoctor(doctorId);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping(value = "/frequent")
    public ResponseEntity<?> getFrequentPatients(@RequestParam long min){
        List<Patient> patients = patServe.getFrequentPatients(min);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }
    
}
