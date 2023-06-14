package ro.dental.clinic.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.dental.clinic.enums.UserStatus;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserEtyTest {

    private UserEty user;

    @BeforeEach
    public void setUp() {
        user = new UserEty();
    }

    @Test
    public void testUsername() {
        String username = "testUser";
        user.setUsername(username);
        assertEquals(username, user.getUsername());
    }

    @Test
    public void testFirstName() {
        String firstName = "John";
        user.setFirstName(firstName);
        assertEquals(firstName, user.getFirstName());
    }

    @Test
    public void testLastName() {
        String lastName = "Doe";
        user.setLastName(lastName);
        assertEquals(lastName, user.getLastName());
    }

    @Test
    public void testEmail() {
        String email = "test@test.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    @Test
    public void testCrtUsr() {
        String crtUsr = "creator";
        user.setCrtUsr(crtUsr);
        assertEquals(crtUsr, user.getCrtUsr());
    }

    @Test
    public void testCrtTms() {
        Instant crtTms = Instant.now();
        user.setCrtTms(crtTms);
        assertEquals(crtTms, user.getCrtTms());
    }

    @Test
    public void testMdfUsr() {
        String mdfUsr = "modifier";
        user.setMdfUsr(mdfUsr);
        assertEquals(mdfUsr, user.getMdfUsr());
    }

    @Test
    public void testMdfTms() {
        Instant mdfTms = Instant.now();
        user.setMdfTms(mdfTms);
        assertEquals(mdfTms, user.getMdfTms());
    }

    @Test
    public void testRole() {
        String role = "Admin";
        user.setRole(role);
        assertEquals(role, user.getRole());
    }

    @Test
    public void testGender() {
        String gender = "Male";
        user.setGender(gender);
        assertEquals(gender, user.getGender());
    }

    @Test
    public void testStatus() {
        UserStatus status = UserStatus.ACTIVE;
        user.setStatus(status);
        assertEquals(status, user.getStatus());
    }

    @Test
    public void testId() {
        String id = "1234";
        user.setUserId(id);
        assertEquals(id, user.getId());
    }
}
