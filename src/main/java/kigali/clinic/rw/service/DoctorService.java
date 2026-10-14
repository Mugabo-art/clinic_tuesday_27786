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
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository docRepo;

    @Autowired
    private SpecializationRepository specRepo;

    @Autowired
    private OfficeRepository offRepo;

    public String saveDoctor(Doctor doctor, UUID officeId) {

        String officeMessage = assignOffice(doctor, officeId, null);
        if (officeMessage != null) {
            return officeMessage;
        }

        docRepo.save(doctor);
        return "Doctor is saved successfully";
    }

    public List<Doctor> getAllDoctors() {
        return docRepo.findAll();
    }

    public Optional<Doctor> getDoctorById(UUID id) {
        return docRepo.findById(id);
    }

    public String updateDoctor(UUID id, Doctor newDoctor, UUID officeId) {

        Optional<Doctor> existing = docRepo.findById(id);
        if (existing.isEmpty()) {
            return "Doctor not found";
        }

        Doctor doctor = existing.get();
        String officeMessage = assignOffice(doctor, officeId, id);
        if (officeMessage != null) {
            return officeMessage;
        }

        doctor.setFirstName(newDoctor.getFirstName());
        doctor.setLastName(newDoctor.getLastName());
        doctor.setDateOfBirth(newDoctor.getDateOfBirth());
        docRepo.save(doctor);
        return "Doctor is updated successfully";
    }

    @Transactional
    public String deleteDoctor(UUID id) {

        Optional<Doctor> existing = docRepo.findById(id);
        if (existing.isEmpty()) {
            return "Doctor not found";
        }

        if (!existing.get().getAppointments().isEmpty()) {
            return "Doctor has appointments and cannot be deleted";
        }

        if (!existing.get().getSpecializations().isEmpty()) {
            return "Doctor has specializations and cannot be deleted";
        }

        docRepo.deleteById(id);
        return "Doctor is deleted successfully";
    }

    public List<Doctor> getDoctorsBySpecialization(String name) {
        return docRepo.findBySpecializationName(name);
    }

    public List<Doctor> getDoctorsWithoutOffice() {
        return docRepo.findWithoutOffice();
    }

    @Transactional
    public String addSpecialization(UUID doctorId, UUID specializationId) {

        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if (doctor.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> specialization = specRepo.findById(specializationId);
        if (specialization.isEmpty()) {
            return "That specialization does not exist";
        }

        if (doctor.get().getSpecializations().contains(specialization.get())) {
            return "The doctor already has that specialization";
        }

        doctor.get().getSpecializations().add(specialization.get());
        docRepo.save(doctor.get());
        return "Specialization added to the doctor";
    }

    @Transactional
    public String removeSpecialization(UUID doctorId, UUID specializationId) {

        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if (doctor.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> specialization = specRepo.findById(specializationId);
        if (specialization.isEmpty()) {
            return "That specialization does not exist";
        }

        if (!doctor.get().getSpecializations().contains(specialization.get())) {
            return "The doctor does not have that specialization";
        }

        doctor.get().getSpecializations().remove(specialization.get());
        docRepo.save(doctor.get());
        return "Specialization removed from the doctor";
    }

    public String changeOffice(UUID doctorId, int officeNumber) {

        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if (doctor.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Office> office = offRepo.findByOfficeNumber(officeNumber);
        if (office.isEmpty()) {
            return "The office with that office number does not exist";
        }

        if (docRepo.existsByOfficeOfficeNumberAndIdNot(officeNumber, doctorId)) {
            return "That office is taken by other doctor";
        }

        doctor.get().setOffice(office.get());
        docRepo.save(doctor.get());
        return "Office changed successfully";
    }

    public List<Doctor> searchByName(String name){
        return docRepo.searchByName(name);
    }

    private String assignOffice(Doctor doctor, UUID officeId, UUID doctorId) {

        if (officeId == null) {
            return null;
        }

        Optional<Office> office = offRepo.findById(officeId);
        if (office.isEmpty()) {
            return "Office not found";
        }

        Optional<Doctor> owner = docRepo.findByOfficeId(officeId);
        if (owner.isPresent() && !owner.get().getId().equals(doctorId)) {
            return "Office is already assigned to another doctor";
        }

        doctor.setOffice(office.get());
        return null;
    }
}
