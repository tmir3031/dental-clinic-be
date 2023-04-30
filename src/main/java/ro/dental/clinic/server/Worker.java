//package ro.dental.clinic.server;
//
//import ro.dental.clinic.domain.AppointmentEty;
//import ro.dental.clinic.service.PatientService;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//import java.util.concurrent.Callable;
//import java.util.concurrent.Future;
//
//public class Worker implements Runnable {
//    private Socket client;
//    private Future<AppointmentEty> future;
//    private PatientService patientService;
//    private ObjectOutputStream outputStream;
//    private ObjectInputStream inputStream;
//
//    public Worker(Future<AppointmentEty> future, Socket client, PatientService patientService, ObjectOutputStream outputStream, ObjectInputStream inputStream) {
//        this.future = future;
//        this.client = client;
//        this.patientService = patientService;
//        this.outputStream = outputStream;
//        this.inputStream = inputStream;
//    }
//
//    @Override
//    public void run() {
//        try {
//            AppointmentEty responseAppointment = future.get();
//            boolean clientResponse = true;
//            if (responseAppointment == null) clientResponse = false;
//            outputStream.writeBoolean(clientResponse);
//            outputStream.flush();
//            if (clientResponse) {
//                outputStream.writeObject(responseAppointment);
//            }
//            outputStream.close();
//            client.close();
//        } catch (Exception e) {
//            try {
//                client.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    public static class ProcessAppointmentTask implements Callable<AppointmentEty> {
//        private ObjectInputStream inputStream;
//        private Socket client;
//
//        public ProcessAppointmentTask(ObjectInputStream inputStream, Socket client) {
//            this.inputStream = inputStream;
//            this.client = client;
//        }
//
//        @Override
//        public AppointmentEty call() throws Exception {
//            AppointmentEty appointment = (AppointmentEty)inputStream.readObject();
//            System.out.println("Am primit programarea " + appointment.toString() + " de la client");
//            patientService.createAppointment(appointment);
//            return appointment;
//        }
//    }
//}
