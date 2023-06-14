package ro.dental.clinic.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatientEtyTest {

    private PatientEty patient;

    @BeforeEach
    public void setUp() {
        patient = new PatientEty();
    }

    @Test
    public void testUser() {
        UserEty user = new UserEty();
        user.setUserId("test");
        patient.setUser(user);
        assertEquals(user, patient.getUser());
    }

    @Test
    public void testChronicDiseases() {
        String chronicDiseases = "Diabetes";
        patient.setChronicDiseases(chronicDiseases);
        assertEquals(chronicDiseases, patient.getChronicDiseases());
    }

    @Test
    public void testAllergies() {
        String allergies = "Peanuts";
        patient.setAllergies(allergies);
        assertEquals(allergies, patient.getAllergies());
    }

    @Test
    public void testDateOfBirth() {
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        patient.setDateOfBirth(dateOfBirth);
        assertEquals(dateOfBirth, patient.getDateOfBirth());
    }

    @Test
    public void testPhone() {
        String phone = "123456789";
        patient.setPhone(phone);
        assertEquals(phone, patient.getPhone());
    }

    @Test
    public void testV() {
        Long v = 10L;
        patient.setV(v);
        assertEquals(v, patient.getV());
    }

    @Test
    public void testAppointmentEtyList() {
        ArrayList<AppointmentEty> appointments = new ArrayList<>();
        appointments.add(new AppointmentEty());
        patient.setAppointmentEtyList(appointments);
        assertEquals(appointments, patient.getAppointmentEtyList());
    }
}
