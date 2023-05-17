package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "PHOTOS", schema = "public")
@Getter
@Setter
@SequenceGenerator(name = "PHOTOS_ID_SQ", sequenceName = "PHOTOS_ID_SQ", allocationSize = 1)

public class Entitate extends SrgKeyEntityTml<Long> {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PHOTOS_ID_SQ")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, targetEntity = PatientEty.class)
        @JoinColumn(name = "PATIENT_ID")
        private PatientEty patient;

        @Column(name = "PHOTOS")
        private String imageURL;

        @Column(name = "DATE")
        private Instant date;

        @Column(name = "V")
        private Long v;

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
