package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "DOCTOR")
public class DoctorEty extends SrgKeyEntityTml<String> {
    @Column(name = "DESCRIPTION")
    private String description;

    @Id
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID")
    @MapsId
    private UserEty user;

    @Column(name = "V")
    private Long v;

    @OneToMany(
            mappedBy = "doctor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AppointmentEty> appointmentEtyList = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "DOCTOR_SPECIALIZATION",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "specialization_id"))
    private List<SpecializationEty> specializationEtyList = new ArrayList<>();

    @Override
    public String getId() {
        return this.user.getUserId();
    }

    @Override
    protected Class<? extends SrgKeyEntityTml<String>> entityRefClass() {
        return DoctorEty.class;
    }

    public void addSpecializationEty(SpecializationEty specializationEty) {
        specializationEtyList.add(specializationEty);
        specializationEty.getDoctorEtyList().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoctorEty)) return false;
        return id != null && id.equals(((DoctorEty) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
