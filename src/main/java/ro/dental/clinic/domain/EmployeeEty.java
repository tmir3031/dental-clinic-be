package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;
import ro.dental.clinic.enums.EmployeeStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "CLIENT")
public class EmployeeEty extends SrgKeyEntityTml<String> {

    @Id
    @Column(name = "USER_ID")
    private String employeeId;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "CRT_USR")
    private String crtUsr;
    @Column(name = "CRT_TMS")
    private Instant crtTms;
    @Column(name = "MDF_USR")
    private String mdfUsr;
    @Column(name = "MDF_TMS")
    private Instant mdfTms;
    @Column(name = "ROLE")
    private String role;
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
    @Column(name = "DESCRIPTION")
    private String description;
    @OneToMany(
            mappedBy = "employee",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AppointmentEty> appointmentEtyList = new ArrayList<>();


    @Override
    protected Class<? extends SrgKeyEntityTml<String>> entityRefClass() {
        return EmployeeEty.class;
    }

    @Override
    public String getId() {
        return this.getEmployeeId();
    }


    public void addAppointment(AppointmentEty appointmentEty) {
        appointmentEtyList.add(appointmentEty);
        appointmentEty.setEmployee(this);
    }

    public void removeAppointment(AppointmentEty appointmentEty) {
        appointmentEtyList.remove(appointmentEty);
        appointmentEty.setEmployee(null);
    }

}
