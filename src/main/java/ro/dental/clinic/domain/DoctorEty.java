package ro.dental.clinic.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.h2.engine.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "DOCTOR")
//@Table(name = "DOCTOR", schema = "public")
//public class DoctorEty extends SrgKeyEntityTml<String> implements Serializable {
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

    @ManyToMany(fetch = FetchType.EAGER)
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

}
