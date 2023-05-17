package ro.dental.clinic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEty, Long> {
    List<AppointmentEty> findAllByDate(LocalDate tomorrow);

    List<AppointmentEty> findAllByPatient(PatientEty patientEty);
}
