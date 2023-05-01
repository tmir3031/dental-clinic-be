package ro.dental.clinic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntitateRepository extends JpaRepository<Entitate, Long> {

    public Entitate getByPatient(PatientEty patientEty);
}
