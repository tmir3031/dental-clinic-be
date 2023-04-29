package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;
import ro.dental.clinic.enums.AppointmentStatus;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "APPOINTMENT", schema = "public")
@Getter
@Setter
@SequenceGenerator(name = "APPOINTMENT_ID_SQ", sequenceName = "APPOINTMENT_ID_SQ", allocationSize = 1)

public class AppointmentEty extends SrgKeyEntityTml<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPOINTMENT_ID_SQ")
    private Long id;

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

    @Column(name = "TREATMENT")
    private String treatment;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = DoctorEty.class)
    @JoinColumn(name = "DOCTOR_ID")
    private DoctorEty doctor;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = PatientEty.class)
    @JoinColumn(name = "PATIENT_ID")
    private PatientEty patient;

    @Override
    protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
        return AppointmentEty.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentEty)) return false;
        return id != null && id.equals(((AppointmentEty) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
