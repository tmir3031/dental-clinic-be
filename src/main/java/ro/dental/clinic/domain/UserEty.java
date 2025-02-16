package ro.dental.clinic.domain;

import lombok.Getter;
import lombok.Setter;
import ro.dental.clinic.enums.UserStatus;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "USR")
public class UserEty extends SrgKeyEntityTml<String> {
    @Id
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "CRT_USR")
    private String crtUsr;
    @Column(name = "CRT_TMS")
    private Instant crtTms;
    @Column(name = "MDF_USR")
    private String mdfUsr;
    @Column(name = "MDF_TMS")
    private Instant mdfTms;
    @Column(name = "ROLE")
    private String role;
    @Column(name = "GENDER")
    private String gender;
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Override
    protected Class<? extends SrgKeyEntityTml<String>> entityRefClass() {
        return UserEty.class;
    }

    @Override
    public String getId() {
        return this.userId;
    }

}