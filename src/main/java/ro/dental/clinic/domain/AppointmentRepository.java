package ro.dental.clinic.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEty, Long> {
    List<AppointmentEty> findByDateAndHourAndEmployee(LocalDate date, String hour, EmployeeEty employee);

}
