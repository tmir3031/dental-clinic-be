package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "DOCTOR_SPECIALIZATION", schema = "public")
@Getter
@Setter
@SequenceGenerator(name = "DOCTOR_SPECIALIZATION_ID_SQ", sequenceName = "DOCTOR_SPECIALIZATION_ID_SQ", allocationSize = 1)

public class SpecializationDoctorEty extends SrgKeyEntityTml<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCTOR_SPECIALIZATION_ID_SQ")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "DOCTOR_ID")
    private DoctorEty doctor;
    @ManyToOne()
    @JoinColumn(name = "SPECIALIZATION_ID")
    private SpecializationEty specialization;
    @Column(name = "V")
    private Long v;


    @Override
    protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
        return SpecializationDoctorEty.class;
    }
}