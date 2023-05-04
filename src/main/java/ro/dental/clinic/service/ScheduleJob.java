package ro.dental.clinic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.domain.AppointmentRepository;
import ro.dental.clinic.email.SenderEmailService;

import java.time.LocalDate;
import java.util.List;

@Component
public class ScheduleJob {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SenderEmailService senderEmailService;

    @Async
    @Transactional
    @Scheduled(cron = "0 0 18 * * *", zone = "Europe/Bucharest") // rulează la 17 zilnic
    public void sendAppointmentReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<AppointmentEty> appointments = appointmentRepository.findAllByDate(tomorrow);
        for (AppointmentEty appointment : appointments) {
            String email = "timonea_raluca@yahoo.com"; // appointment.getPatient().getUser().getEmail();
            String subject = "Reminder: Appointment Tomorrow";
            String body = "Dear " + appointment.getPatient().getUser().getLastName()+ ",\n\n" +
                    "This is a friendly reminder that you have an appointment tomorrow at " +
                    appointment.getHour() + ".\n\n" +
                    "Please arrive on time and bring any necessary documents or materials.\n\n" +
                    "Thank you,\n" +
                    "Your Healthcare Team";
            senderEmailService.sendEmail(email,subject,body);
        }
    }
}
