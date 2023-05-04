package ro.dental.clinic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntitateRepository extends JpaRepository<Entitate, Long> {

    public Entitate getByPatient(PatientEty patientEty);
    public List<Entitate> getAllByPatient(PatientEty patientEty);
}
