package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@SequenceGenerator(name = "USER_SPECIALIZATION_ID_SQ", sequenceName = "USER_SPECIALIZATION_ID_SQ", allocationSize = 1)
@Getter
@Setter
@Table(name = "USER_SPECIALIZATION")
public class SpecializationUserEty {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SPECIALIZATION_ID_SQ")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private EmployeeEty employee;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIALIZATION_ID")
    private SpecializationEty specializationEty;

}