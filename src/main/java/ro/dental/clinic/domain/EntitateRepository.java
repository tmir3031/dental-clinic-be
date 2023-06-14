package ro.dental.clinic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntitateRepository extends JpaRepository<Entitate, Long> {
    List<Entitate> getAllByPatient(PatientEty patientEty);
}
