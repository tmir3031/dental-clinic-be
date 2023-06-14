package ro.dental.clinic.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoctorEtyTest {

    private DoctorEty doctor;

    @BeforeEach
    public void setUp() {
        doctor = new DoctorEty();
    }

    @Test
    public void testDescription() {
        String description = "This is a description";
        doctor.setDescription(description);
        assertEquals(description, doctor.getDescription());
    }

    @Test
    public void testUser() {
        UserEty user = new UserEty();
        user.setUserId("test");
        doctor.setUser(user);
        assertEquals(user, doctor.getUser());
    }

    @Test
    public void testV() {
        Long v = 10L;
        doctor.setV(v);
        assertEquals(v, doctor.getV());
    }

    @Test
    public void testAppointmentEtyList() {
        ArrayList<AppointmentEty> appointments = new ArrayList<>();
        appointments.add(new AppointmentEty());
        doctor.setAppointmentEtyList(appointments);
        assertEquals(appointments, doctor.getAppointmentEtyList());
    }

    @Test
    public void testSpecializationEtyList() {
        ArrayList<SpecializationEty> specializations = new ArrayList<>();
        SpecializationEty specialization = new SpecializationEty();
        specializations.add(specialization);
        doctor.setSpecializationEtyList(specializations);
        assertEquals(specializations, doctor.getSpecializationEtyList());
    }

    @Test
    public void testAddSpecializationEty() {
        SpecializationEty specialization = new SpecializationEty();
        doctor.addSpecializationEty(specialization);
        assertTrue(doctor.getSpecializationEtyList().contains(specialization));
    }

    @Test
    public void testHashCode() {
        int expectedHashCode = doctor.getClass().hashCode();
        assertEquals(expectedHashCode, doctor.hashCode());
    }
}
