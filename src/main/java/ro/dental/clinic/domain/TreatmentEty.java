package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "TREATMENT_ID_SQ", sequenceName = "TREATMENT_ID_SQ", allocationSize = 1)
@Getter
@Setter
@Table(name = "TREATMENT")
public class TreatmentEty extends SrgKeyEntityTml<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TREATMENT_ID_SQ")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIALIZATION_ID")
    private SpecializationEty specialization;
    @Column(name = "PRICE")
    private String price;
    @Column(name = "NAME")
    private String name;
    @Column(name = "V")
    private Long v;

    @Override
    protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
        return TreatmentEty.class;
    }
}