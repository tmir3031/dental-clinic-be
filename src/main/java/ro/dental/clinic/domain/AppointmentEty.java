package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;
import ro.dental.clinic.enums.AppointmentStatus;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "APPOINTMENT")
@Getter
@Setter
@SequenceGenerator(name = "APPOINTMENT_ID_SQ", sequenceName = "APPOINTMENT_ID_SQ", allocationSize = 1)

public class AppointmentEty extends SrgKeyEntityTml<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPOINTMENT_ID_SQ")
    private Long id;

    @Column(name = "CRT_USR")
    private String crtUsr;

    @Column(name = "CRT_TMS")
    private Instant crtTms;

    @Column(name = "MDF_USR")
    private String mdfUsr;

    @Column(name = "MDF_TMS")
    private Instant mdfTms;

    @Column(name = "DATE")
    private LocalDate date;

    @Column(name = "HOUR")
    private String hour;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SPECIALIZATION")
    private String specialization;

    @Column(name = "REJECT_REASON")
    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCTOR_ID")
    private EmployeeEty employee;

    @Override
    protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
        return AppointmentEty.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentEty )) return false;
        return id != null && id.equals(((AppointmentEty) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
