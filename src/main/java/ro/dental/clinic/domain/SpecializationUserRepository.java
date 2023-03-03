package ro.dental.clinic.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface SpecializationUserRepository extends
        JpaRepository<SpecializationUserEty, Long>,
        QuerydslPredicateExecutor<SpecializationUserEty> {

    Optional<SpecializationUserEty> findByEmployee(EmployeeEty employeeEty);
}
