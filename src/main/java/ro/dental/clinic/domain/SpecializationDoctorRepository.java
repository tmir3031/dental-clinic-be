package ro.dental.clinic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecializationDoctorRepository extends
        JpaRepository<SpecializationDoctorEty, Long> {
}
