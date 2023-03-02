package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    @Column(name = "DESCRIPTION")
    private String description;

    @Override
    protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
        return SpecializationEty.class;
    }
}
