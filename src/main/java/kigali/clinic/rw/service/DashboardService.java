package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class DashboardService {

    @Autowired
    private PatientRepository patRepo;

    @Autowired
    private DoctorRepository docRepo;

    @Autowired
    private OfficeRepository offRepo;

    @Autowired
    private AppointmentRepository appRepo;

    @Autowired
    private PrescriptionRepository presRepo;

    public Map<String, Long> getSummary(){

        Map<String, Long> summary = new LinkedHashMap<>();

        List<Office> freeOffices = offRepo.findFreeOffices();
        long freeOfficeCount = freeOffices.size();

        summary.put("patients", patRepo.count());
        summary.put("doctors", docRepo.count());
        summary.put("freeOffices", freeOfficeCount);
        summary.put("appointmentsToday", appRepo.countByAppointmentDate(LocalDate.now()));
        summary.put("scheduled", appRepo.countByStatus(AppointmentStatus.SCHEDULED));
        summary.put("prescriptions", presRepo.count());

        return summary;
    }

}
