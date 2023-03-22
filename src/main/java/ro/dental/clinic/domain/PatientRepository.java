package ro.dental.clinic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<PatientEty, String> {
}

