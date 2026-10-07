package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specRepo;

    @Autowired
    private DoctorRepository docRepo;


    public String saveSpecialization(Specialization specialization){

        if(specRepo.findByName(specialization.getName()).isPresent()){
            return "Specialization name already exists";
        }
        specRepo.save(specialization);
        return "Specialization is saved successfully";
    }

    public List<Specialization> getAllSpecializations(){
        return specRepo.findAll();
    }

    public Optional<Specialization> getSpecializationById(UUID id){
        return specRepo.findById(id);
    }

    public String updateSpecialization(UUID id, Specialization newSpecialization){

        Optional<Specialization> existing = specRepo.findById(id);
        if(existing.isEmpty()){
            return "Specialization not found";
        }

        Optional<Specialization> sameName = specRepo.findByName(newSpecialization.getName());
        if(sameName.isPresent() && !sameName.get().getId().equals(id)){
            return "Specialization name already exists";
        }

        Specialization specialization = existing.get();
        specialization.setName(newSpecialization.getName());
        specRepo.save(specialization);
        return "Specialization is updated successfully";
    }

    @Transactional
    public String deleteSpecialization(UUID id){

        if(!specRepo.existsById(id)){
            return "Specialization not found";
        }

        specRepo.deleteById(id);
        return "Specialization is deleted successfully";
    }

    public List<Specialization> getUnusedSpecializations(){
        return specRepo.findUnused();
    }

    @Transactional
    public String addDoctor(UUID specializationId, UUID doctorId){

        Optional<Specialization> specialization = specRepo.findById(specializationId);
        if(specialization.isEmpty()){
            return "Specialization not found";
        }

        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if(doctor.isEmpty()){
            return "Doctor not found";
        }

        if(specialization.get().getDoctors().contains(doctor.get())){
            return "Doctor already has this specialization";
        }

        specialization.get().getDoctors().add(doctor.get());
        specRepo.save(specialization.get());
        return "Doctor is added to specialization successfully";
    }

    @Transactional
    public String removeDoctor(UUID specializationId, UUID doctorId){

        Optional<Specialization> specialization = specRepo.findById(specializationId);
        if(specialization.isEmpty()){
            return "Specialization not found";
        }

        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if(doctor.isEmpty()){
            return "Doctor not found";
        }

        if(!specialization.get().getDoctors().contains(doctor.get())){
            return "Doctor does not have this specialization";
        }

        specialization.get().getDoctors().remove(doctor.get());
        specRepo.save(specialization.get());
        return "Doctor is removed from specialization successfully";
    }
}
