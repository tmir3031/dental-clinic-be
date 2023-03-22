package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@SequenceGenerator(name = "DOCTOR_SPECIALIZATION_ID_SQ", sequenceName = "DOCTOR_SPECIALIZATION_ID_SQ", allocationSize = 1)
@Getter
@Setter
@Table(name = "DOCTOR_SPECIALIZATION")
public class SpecializationDoctorEty extends SrgKeyEntityTml<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCTOR_SPECIALIZATION_ID_SQ")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCTOR_ID")
    private DoctorEty doctor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIALIZATION_ID")
    private SpecializationEty specialization;

    @Override
    protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
        return SpecializationDoctorEty.class;
    }
}