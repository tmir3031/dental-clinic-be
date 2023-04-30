package ro.dental.clinic.server;

import ro.dental.clinic.service.ServiceEmail;

public class LoggerForEmail implements Runnable {
    private final ServiceEmail service;

    public LoggerForEmail(ServiceEmail service) {
        this.service = service;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50000000);
                service.verify();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
