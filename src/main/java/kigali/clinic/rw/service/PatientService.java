package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.PatientRepository;

@Service 
public class PatientService {

    @Autowired 
    private PatientRepository patRepo;


    public String savePatient(Patient patient){
        patRepo.save(patient);
        return "Patient is saved successfully";
    }

    public List<Patient> getAllPatients(){
        return patRepo.findAll();
    }

    public Optional<Patient> getPatientById(UUID id){
        return patRepo.findById(id);
    }

    public String updatePatient(UUID id, Patient newPatient){

        Optional<Patient> existing = patRepo.findById(id);
        if(existing.isEmpty()){
            return "Patient not found";
        }

        Patient patient = existing.get();
        patient.setFirstName(newPatient.getFirstName());
        patient.setLastName(newPatient.getLastName());
        patient.setDateOfBirth(newPatient.getDateOfBirth());
        patient.setSsNumber(newPatient.getSsNumber());
        patRepo.save(patient);
        return "Patient is updated successfully";
    }

    @Transactional
    public String deletePatient(UUID id){

        Optional<Patient> existing = patRepo.findById(id);
        if(existing.isEmpty()){
            return "Patient not found";
        }

        if(!existing.get().getAppointments().isEmpty()){
            return "Patient has appointments and cannot be deleted";
        }

        patRepo.deleteById(id);
        return "Patient is deleted successfully";
    }

    public List<Patient> getPatientsByLastName(String lastName){
        return patRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public List<Patient> getPatientsOfDoctor(UUID doctorId){
        return patRepo.findPatientsOfDoctor(doctorId);
    }

    public List<Patient> getFrequentPatients(long min){
        return patRepo.findFrequentPatients(min);
    }
}
