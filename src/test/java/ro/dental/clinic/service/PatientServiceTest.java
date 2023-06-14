package ro.dental.clinic.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.dental.clinic.domain.PatientEty;
import ro.dental.clinic.domain.PatientRepository;
import ro.dental.clinic.domain.UserEty;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.model.PatientCreationRequest;
import ro.dental.clinic.model.PatientUpdateRequest;
import ro.dental.clinic.model.UserDetails;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @InjectMocks
    PatientService patientService;

    @Mock
    PatientRepository patientRepository;

    @Mock
    KeycloakClientApi keycloakClientApi;

    @Test
    void createPatient2() {
        PatientEty savedPatient = new PatientEty();
        UserDetails userDetails = new UserDetails();

        when(patientRepository.save(any(PatientEty.class))).thenReturn(savedPatient);
        when(keycloakClientApi.createUser(any(UserDetails.class))).thenReturn(userDetails);

        assertThatCode(() -> {
            PatientCreationRequest request = new PatientCreationRequest();
            patientService.createPatient(request);
        }).doesNotThrowAnyException();

        verify(patientRepository, times(1)).save(any(PatientEty.class));
    }


    @Test
    void createPatient() {
        var savedPatient = new PatientEty();

        when(patientRepository.save(any(PatientEty.class))).thenReturn(savedPatient);
        doReturn(new UserDetails()).when(keycloakClientApi).createUser(any());

        assertThatCode(() -> patientService.createPatient(new PatientCreationRequest())).doesNotThrowAnyException();

        verify(patientRepository, times(1)).save(any());
    }

    @Test
    void updateEmployeeNotExistingInDB() {
        var patientUpdateRequest = new PatientUpdateRequest();
        when(patientRepository.findById("256784")).thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessException.class,
                () -> patientService.updatePatient("1456788", patientUpdateRequest));
        verify(patientRepository).findById(anyString());
    }

    @Test
    void createEmployeeInsucess() {
        var savedPatient = new PatientEty();
        savedPatient.setAllergies("nuu");

        var request = new PatientCreationRequest();
        request.setPhone("071231232");

        PatientRepository patientRepository = mock(PatientRepository.class);
        KeycloakClientApi keycloakClientApi = mock(KeycloakClientApi.class);

        when(patientRepository.save(any(PatientEty.class))).thenReturn(savedPatient);
        doReturn(new UserDetails()).when(keycloakClientApi).createUser(any());

        assertThatCode(() -> patientService.createPatient(request)).doesNotThrowAnyException();

        verify(patientRepository, times(2)).save(any());
    }


    @Test
    void getPatientById_patientExists_returnsPatientCreationRequest() {
        String patientId = "1";
        PatientRepository patientRepository = mock(PatientRepository.class);
        PatientEty patientEty = new PatientEty();
        patientEty.setId(patientId);
        patientEty.setUser(new UserEty());

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patientEty));

        PatientHandler patientHandler = new PatientHandler(patientRepository);

        PatientCreationRequest result = patientHandler.handlePatientDetails(patientId);

        assertNotNull(result);
        assertEquals(patientEty.getUser().getEmail(), result.getEmail());
        assertEquals(patientEty.getUser().getGender(), result.getGender());
        assertEquals(patientEty.getAllergies(), result.getAllergies());
    }

    @Test
    void getPatientById_patientDoesNotExist_throwsException() {
        String patientId = "1";
        PatientRepository patientRepository = mock(PatientRepository.class);
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        PatientHandler patientHandler = new PatientHandler(patientRepository);

        assertThrows(RuntimeException.class, () -> patientHandler.handlePatientDetails(patientId));
    }

//    @Test
//    void createAppointment_appointmentCanBeCreated_createsAppointment() {
//        String patientId = "1";
//        AppointmentCreationRequest creationRequest = new AppointmentCreationRequest();
//        creationRequest.setDoctorId("2");
//        creationRequest.setDate(LocalDate.now());
//        creationRequest.setHour("10:00");
//
//        PatientRepository patientRepository = mock(PatientRepository.class);
//        DoctorRepository doctorRepository = mock(DoctorRepository.class);
//        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
//        TimeManager timeManager = mock(TimeManager.class);
//
//        when(patientRepository.findById(patientId)).thenReturn(Optional.of(new PatientEty()));
//        when(doctorRepository.findById(creationRequest.getDoctorId())).thenReturn(Optional.of(new DoctorEty()));
//        when(appointmentRepository.findAll()).thenReturn(new ArrayList<>());
//        when(timeManager.instant()).thenReturn(Instant.now());
//        doThrow(new BusinessException(Collections.singletonList(BusinessException.BusinessExceptionElement.builder()
//                .errorCode(BusinessErrorCode.USER_NOT_FOUND)
//                .build())))
//            .when(appointmentRepository).save(any(AppointmentEty.class));
//
//
//        BusinessException exception = assertThrows(BusinessException.class,
//                () -> patientService.createAppointment(patientId, creationRequest));
//        assertEquals(BusinessErrorCode.USER_NOT_FOUND,
//                exception.getErrors().get(0).getErrorCode());
//
//        verify(appointmentRepository, times(1)).save(any(AppointmentEty.class));
//    }


}

//package ro.dental.clinic.service;
//
//import org.junit.Before;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.test.context.junit4.SpringRunner;
//import ro.dental.clinic.domain.PatientEty;
//import ro.dental.clinic.domain.PatientRepository;
//import ro.dental.clinic.mapper.PatientCreationRequestToUserDetailsMapper;
//import ro.dental.clinic.mapper.PatientMapper;
//import ro.dental.clinic.model.PatientCreationRequest;
//import ro.dental.clinic.model.UserDetails;
//import ro.dental.clinic.utils.TimeManager;
//
//import java.time.Instant;
//import java.time.LocalDate;
//
//import static org.mockito.Mockito.*;
//
//@RunWith(SpringRunner.class)
//public class PatientServiceTest {
//
//    @InjectMocks
//    private PatientService patientService;
//
//    @Mock
//    private PatientRepository patientRepository;
//
//    @Mock
//    private KeycloakClientApi keycloakClientApi;
//
//    @Mock
//    private PatientMapper patientMapper;
//
//    @Mock
//    private PatientCreationRequestToUserDetailsMapper userDetailsMapper;
//
//    @Mock
//    private TimeManager timeManager;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void createPatient_success() {
//        PatientCreationRequest request = new PatientCreationRequest();
//        request.setUsername("testUsername");
//        request.setPassword("testPassword");
//        request.setFirstName("testFirstName");
//        request.setLastName("testLastName");
//        request.setEmail("test_test@yahoo.com");
//        request.setRole("USER");
//        request.setGender("masculin");
//        request.setChronicDiseases("testDisease");
//        request.setAllergies("testAllergy");
//        request.setDateOfBirth(LocalDate.now());
//        request.setPhone("1234567890");
//
//        PatientEty patientEty = patientMapper.mapPatientCreationRequestToPatientEty(request);
//        when(patientMapper.mapPatientCreationRequestToPatientEty(request)).thenReturn(patientEty);
//
//        UserDetails userDetails = new UserDetails();
//        when(userDetailsMapper.toUserDetails(request)).thenReturn(userDetails);
//
//        when(keycloakClientApi.createUser(userDetails)).thenReturn(userDetails);
//
//        Instant now = Instant.now();
//        when(timeManager.instant()).thenReturn(now);
//        patientService.createPatient(request);
//        verify(patientRepository, times(1)).save(patientEty);
//    }
//}
