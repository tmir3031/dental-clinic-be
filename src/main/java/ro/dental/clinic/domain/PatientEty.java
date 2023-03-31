package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "PATIENT")
public class PatientEty extends SrgKeyEntityTml<String> implements Serializable {
    @Id
    private String id;
    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "USER_ID")
    private UserEty user;

    @Column(name = "CHRONIC_DISEASES")
    private String chronicDiseases;

    @Column(name = "ALLERGIES")
    private String allergies;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "V")
    private Long v;
    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AppointmentEty> appointmentEtyList = new ArrayList<>();

    @Override
    public String getId() {
        return this.user.getUserId();
    }
    @Override
    protected Class<? extends SrgKeyEntityTml<String>> entityRefClass() {
        return PatientEty.class;
    }
}
