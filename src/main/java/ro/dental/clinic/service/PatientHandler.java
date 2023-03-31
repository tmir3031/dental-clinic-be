package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.dental.clinic.domain.PatientEty;
import ro.dental.clinic.domain.PatientRepository;
import ro.dental.clinic.domain.UserRepository;
import ro.dental.clinic.mapper.PatientMapper;
import ro.dental.clinic.model.PatientCreationRequest;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PatientHandler {
    private final PatientRepository patientRepository;

    public PatientCreationRequest handlePatientDetails(String patientId) {
        Optional<PatientEty> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Pacientul cu id-ul " + patientId + " nu exista.");
        }
        var patient = patientOpt.get();
        PatientCreationRequest patientCreationRequest = new PatientCreationRequest();
        patientCreationRequest.setEmail(patient.getUser().getEmail());
        patientCreationRequest.setGender(patient.getUser().getGender());
        patientCreationRequest.setAllergies(patient.getAllergies());
        patientCreationRequest.setRole(patient.getUser().getRole());
        patientCreationRequest.setUsername(patient.getUser().getUsername());
        patientCreationRequest.setPhone(patient.getPhone());
        patientCreationRequest.setChronicDiseases(patient.getChronicDiseases());
        patientCreationRequest.setDateOfBirth(patient.getDateOfBirth());
        patientCreationRequest.setLastName(patient.getUser().getLastName());
        patientCreationRequest.setFirstName(patient.getUser().getFirstName());
        return patientCreationRequest;

    }
}
