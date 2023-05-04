package ro.dental.clinic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ro.dental.clinic.service.ScheduleJob;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DentalClinicApplication {

	public static void main(String[] args) {
		SpringApplication.run(DentalClinicApplication.class, args);
	}

	@Autowired
	private ScheduleJob scheduleJob;

	@PostConstruct
	public void scheduleEmailJob() {
		scheduleJob.sendAppointmentReminders();
	}

}
