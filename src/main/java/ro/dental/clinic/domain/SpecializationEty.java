package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(name = "SPECIALIZATION_ID_SQ", sequenceName = "SPECIALIZATION_ID_SQ", allocationSize = 1)
@Getter
@Setter
@Table(name = "SPECIALIZATION")
public class SpecializationEty extends SrgKeyEntityTml<Long> {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SPECIALIZATION_ID_SQ")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "V")
    private Long v;
//
//    @OneToMany(
//            mappedBy = "specialization",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
//    private List<SpecializationDoctorEty> specializationDoctorEtyList = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "DOCTOR_SPECIALIZATION",
            joinColumns = @JoinColumn(name = "specialization_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id"))
    private List<DoctorEty> doctorEtyList = new ArrayList<>();


    @Override
    protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
        return SpecializationEty.class;
    }
}
