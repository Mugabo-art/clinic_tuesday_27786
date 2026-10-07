package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;

@Service 
public class OfficeService {

    @Autowired 
    private OfficeRepository offRepo;

    @Autowired 
    private DoctorRepository docRepo;


    public String saveOffice(Office office){

        if(offRepo.findByOfficeNumber(office.getOfficeNumber()).isPresent()){
            return "Office number already exists";
        }
        offRepo.save(office);
        return "Office is saved successfully";
    }

    public List<Office> getAllOffices(){
        return offRepo.findAll();
    }

    public Optional<Office> getOfficeById(UUID id){
        return offRepo.findById(id);
    }

    public String updateOffice(UUID id, Office newOffice){

        Optional<Office> existing = offRepo.findById(id);
        if(existing.isEmpty()){
            return "Office not found";
        }

        Optional<Office> sameNumber = offRepo.findByOfficeNumber(newOffice.getOfficeNumber());
        if(sameNumber.isPresent() && !sameNumber.get().getId().equals(id)){
            return "Office number already exists";
        }

        Office office = existing.get();
        office.setName(newOffice.getName());
        office.setOfficeNumber(newOffice.getOfficeNumber());
        offRepo.save(office);
        return "Office is updated successfully";
    }

    @Transactional
    public String deleteOffice(UUID id){

        if(!offRepo.existsById(id)){
            return "Office not found";
        }

        Optional<Doctor> doctor = docRepo.findByOfficeId(id);
        if(doctor.isPresent()){
            doctor.get().setOffice(null);
            docRepo.save(doctor.get());
        }

        offRepo.deleteById(id);
        return "Office is deleted successfully";
    }

    public List<Object[]> getBusiestOffice(){
        return offRepo.countAppointmentsPerOffice();
    }
}
