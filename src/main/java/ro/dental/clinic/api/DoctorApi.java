package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.email.SenderEmailService;
import ro.dental.clinic.model.*;
import ro.dental.clinic.service.AppointmentService;
import ro.dental.clinic.service.DoctorService;
import ro.dental.clinic.service.PatientService;
import ro.dental.clinic.service.PhotoService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/doctors")
public class DoctorApi {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final PhotoService photoService;
    private final SenderEmailService senderEmailService;

    @PostMapping("/create")
    public ResponseEntity<Void> createDoctor(@Valid @RequestBody DoctorCreationRequest doctorCreationRequest) {
        var password = doctorService.generatePassword(8);
        doctorCreationRequest.setPassword(password);
        doctorService.createDoctor(doctorCreationRequest);
        sendEmailWithPasswordForADoctor(doctorCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long appointmentId) {
        var appointment = appointmentService.getAppointmentById(appointmentId);
        sendEmailForDeleteAnAppointment(appointment);
        doctorService.deleteAppointment(appointmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/my-patients/{doctorId}")
    public ResponseEntity<PatientDetailList> getAllPatientsForADoctor(@PathVariable String doctorId) {
        return ResponseEntity.ok(patientService.getAllPatientDTOsForADoctor(doctorId));
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorDetailListItem> getDoctorById(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
    }

    @GetMapping
    public ResponseEntity<DoctorDetailList> getDoctorDetailsList(@RequestParam(value = "specializationId", required = false) Long specializationId) {
        return ResponseEntity.ok(doctorService.getDoctorDetails(specializationId));
    }

    @GetMapping("/view-all-image/{userId}")
    public ResponseEntity<List<ImageModel>> getRadiographsForAPatient(@PathVariable String userId) {
        return ResponseEntity.ok(photoService.getRadiographsForAPatient(userId));
    }

    @PatchMapping("/{doctorId}/appointments/{appointmentId}")
    @Secured({"ROLE_DOCTOR"})
    public ResponseEntity<Void> patchAppointment(@PathVariable String doctorId, @PathVariable Long appointmentId, @RequestBody AppointmentReview appointmentReview) {
        doctorService.patchAppointment(doctorId, appointmentId, appointmentReview);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/save-image/{userId}")
    public ResponseEntity<Void> saveRadiography(@PathVariable String userId, @RequestParam("image") MultipartFile radiography) {
        photoService.saveRadiography(radiography, userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public void sendEmailWithPasswordForADoctor(DoctorCreationRequest doctorCreationRequest) {
        String email = "timonea_raluca@yahoo.com"; // userCreationRequest.getEmail();
        String subject = "Bun venit in echipa DentalConnect";
        String emailDoctor = doctorCreationRequest.getEmail();
        String usernameDoctor = doctorCreationRequest.getUsername();
        String passwordDoctor = doctorCreationRequest.getPassword();

        String body = "Bună ziua,\n\n"
                + "Vă urez un călduros \"Bun venit!\" în echipa noastră medicală. Suntem încântați să vă avem alături și suntem convinși că veți aduce o contribuție valoroasă în asigurarea serviciilor noastre de sănătate de înaltă calitate.\n\n"
                + "Vă rugăm să accesați adresa de e-mail alocată pentru dumneavoastră, " + emailDoctor + ", utilizând următoarele credențiale:\n\n"
                + "- Nume de utilizator (Username): " + usernameDoctor + "\n"
                + "- Parolă (Password): " + passwordDoctor + "\n\n"
                + "Această adresă de e-mail vă va permite să comunicați cu echipa noastră, să primiți informații relevante despre programările pacienților și să colaborați cu colegii dumneavoastră în cadrul procesului de îngrijire medicală.\n\n"
                + "Dacă întâmpinați orice dificultăți sau aveți întrebări, nu ezitați să ne contactați. Suntem aici pentru a vă oferi orice asistență necesară și pentru a vă sprijini în activitatea dumneavoastră profesională.\n\n"
                + "Încă o dată, vă mulțumim că v-ați alăturat echipei noastre și vă dorim succes în noua dumneavoastră poziție.\n\n"
                + "Cu stimă,\nEchipa de management";

        senderEmailService.sendEmail(email, body, subject);
    }

    public void sendEmailForDeleteAnAppointment(AppointmentEty appointment) {
        String email = "timonea_raluca@yahoo.com"; // appointment.getPatient().getUser().getEmail();
        String subject = "Programare anulata";
        String body = "Îți transmitem această notificare importantă în legătură cu programarea ta pe data de " + appointment.getDate() + ", la ora " + appointment.getHour() + ".\n\n" + "Trebuie să te anunțăm că programarea ta a fost anulată." + "Dorim să te asigurăm că vom face tot posibilul pentru a te ajuta să obții o nouă programare în cel mai scurt timp posibil. Ne pare rău pentru orice inconvenient.\n\n" + "Multimim pentru intelegere!\n" + "Cu respect,\n" + "Echipa DentalConnect";
        senderEmailService.sendEmail(email, body, subject);
    }

}
