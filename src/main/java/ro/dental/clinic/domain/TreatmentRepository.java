package ro.dental.clinic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentRepository extends JpaRepository<TreatmentEty, Long> {
}
