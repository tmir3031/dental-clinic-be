package ro.dental.clinic.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.domain.AppointmentRepository;
import ro.dental.clinic.email.SenderEmailService;

import java.time.LocalDate;

@Component
@Slf4j
@EnableScheduling
public class SendEmailRemainderScheduleTask {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SenderEmailService senderEmailService;

    @Async
    @Transactional
    @Scheduled(cron = "${ro.dental.clinic.sync.cron}")
    public void sendAppointmentReminders() {
        log.info("Start finding tomorrow appointments in order to send reminder email");
        appointmentRepository.findAllByDate(LocalDate.now().plusDays(1)).forEach(appointment -> {
            String email = "timonea_raluca@yahoo.com"; //appointment.getPatient().getUser().getEmail();
            String subject = "Reamintire: Programare mâine";
            String body = "Stimate/Stimată " + appointment.getPatient().getUser().getLastName() + ",\n\n" +
                    "Acesta este un mesaj pentru a vă reaminti că aveți o programare mâine la ora " +
                    appointment.getHour() + ".\n\n" +
                    "Vă rugăm să ajungeți la timp!.\n\n" +
                    "Vă mulțumim,\n" +
                    "Echipa dvs. de sănătate";
            senderEmailService.sendEmail(email, body, subject);
        });
    }
}
