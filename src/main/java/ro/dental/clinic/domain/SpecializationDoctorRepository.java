package ro.dental.clinic.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;


public interface SpecializationDoctorRepository extends
        JpaRepository<SpecializationDoctorEty, Long>
         {
}


//public interface SpecializationDoctorRepository extends
//        JpaRepository<SpecializationDoctorEty, Long>,
//        QuerydslPredicateExecutor<SpecializationDoctorEty> {
//}
