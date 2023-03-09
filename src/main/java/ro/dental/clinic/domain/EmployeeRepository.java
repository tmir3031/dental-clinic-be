package ro.dental.clinic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeEty, String> {
    List<EmployeeEty> findByRole(String role);

}
