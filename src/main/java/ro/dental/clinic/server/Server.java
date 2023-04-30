//package ro.dental.clinic.server;
//
//import lombok.RequiredArgsConstructor;
//import ro.dental.clinic.service.PatientService;
//import ro.dental.clinic.service.ServiceEmail;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//
//@RequiredArgsConstructor
//public class Server {
//
//    private int port;
//    private ServerSocket serverSocket;
//    public static ExecutorService threadPool;
//    public static PatientService service;
//    public static ServiceEmail serviceEmail;
//    private int noSecondsIdle;
//
//    public void start() {
//        try {
//            serverSocket = new ServerSocket(port);
//            System.out.println("Serverul s-a deschis");
//
//            threadPool.execute(new LoggerForEmail(serviceEmail)); //adaugam in threadPool Logger-ul (adica verificatorul)
//
//            boolean finish = false;
//            while (!finish) {
//                System.out.println("Astept client...");
//                Socket client = serverSocket.accept();
//                System.out.println("Client conectat");
//
//                //procesam programarea de la client
//                processRequest(client);
//            }
//            threadPool.awaitTermination(noSecondsIdle, TimeUnit.SECONDS);
//            serverSocket.close();
//            System.out.println("Serverul s-a inchis");
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void processRequest(Socket client) {
//        ObjectInputStream inputStream = null;
//        ObjectOutputStream outputStream = null;
//        try {
//            inputStream = new ObjectInputStream(client.getInputStream());
//
//
//            outputStream = new ObjectOutputStream(client.getOutputStream());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Procesez cerere de adaugare programare");
//        Future<Treatment> future = threadPool.submit(new ProcessReservationTask(inputStream));
//        Thread thread = new Thread(new Worker(future, client, service, outputStream, inputStream));
//        thread.setDaemon(false);
//        thread.start();
//
//    }
//
//    private class ProcessReservationTask implements Callable<Treatment> {
//        private ObjectInputStream inputStream;
//
//        public ProcessReservationTask(ObjectInputStream inputStream) {
//            this.inputStream = inputStream;
//        }
//
//        @Override
//        public Treatment call() throws Exception {
//            Reservation reservation = (Reservation) inputStream.readObject();
//            System.out.println("Am primit programarea " + reservation.toString() + " de la client");
//            Treatment response = service.addReservation(reservation);
//            return response;
//        }
//    }
//}
