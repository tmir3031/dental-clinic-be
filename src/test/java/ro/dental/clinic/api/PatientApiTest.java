package ro.dental.clinic.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ro.dental.clinic.email.SenderEmailService;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.model.PatientCreationRequest;
import ro.dental.clinic.service.PatientService;

import javax.inject.Inject;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PatientApi.class)
@ContextConfiguration(classes = {ApiExceptionHandler.class, PatientApi.class})
@WithMockUser(username = "test.user", roles = {"USER"})
class EmployeeApiTest {

    @MockBean
    private PatientService patientService;

    @MockBean
    private SenderEmailService senderEmailService;

    @Inject
    private WebApplicationContext webApplicationContext;

    @Inject
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }


    @Test
    void createPatientFoundInDB() throws Exception {
        doNothing().when(patientService).createPatient(any(PatientCreationRequest.class));

        mockMvc.perform(post("/api/v1/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PatientCreationRequest())))
                .andExpect(status().isCreated());

        verify(patientService).createPatient(any());
    }

    @Test
    void createPatientNotFoundInDB() throws Exception {
        doThrow(new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                .errorCode(BusinessErrorCode.USER_NOT_FOUND)
                .build()))).when(patientService).createPatient(any());

        mockMvc.perform(post("/api/v1/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PatientCreationRequest())))
                .andExpect(status().isBadRequest());
    }

}