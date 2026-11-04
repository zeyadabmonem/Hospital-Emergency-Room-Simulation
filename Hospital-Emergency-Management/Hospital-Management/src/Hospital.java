import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Hospital {
    private WaitingRoom waitingRoom;
    private TreatmentRooms treatmentRooms;
    private List<Doctor> doctors;
    private List<Patient> patients;

    private int WaitingRoomCapacity;
    private int NumofPatient;
    private int NumofDoctors;

    private List<Thread> doctorThreads;
    private List<Thread> patientThreads;

    public static Semaphore treated = new Semaphore(0);

    public void getUserInput() {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter Waiting room capacity:");
        WaitingRoomCapacity = input.nextInt();

        System.out.println("Enter Number of doctors:");
        NumofDoctors = input.nextInt();

        System.out.println("Number of patients:");
        NumofPatient = input.nextInt();
    }

    public void initializeHospital() {
        waitingRoom = new WaitingRoom(WaitingRoomCapacity);
        treatmentRooms = new TreatmentRooms();
        doctors = new ArrayList<>();
        patients = new ArrayList<>();
        doctorThreads = new ArrayList<>();
        patientThreads = new ArrayList<>();

        for (int i = 1; i <= NumofDoctors; i++) {
            Doctor doctor = new Doctor(i, waitingRoom, treatmentRooms);

            doctors.add(doctor);
            doctorThreads.add(new Thread(doctor));
        }
        for (int i = 1; i <= NumofPatient; i++) {
            Patient patient = new Patient(i, waitingRoom);

            patients.add(patient);
            patientThreads.add(new Thread(patient));
        }
    }

    public void startSimulation() {
        System.out.println("Patients arriving:");
        for (Patient patient : patients) {
            System.out.print("P" + patient.getId() + " ");
        }
        
        System.out.println();

        for (Thread Doctorthread : doctorThreads) {
            Doctorthread.start();

        }

        for (Thread patientThread : patientThreads) {
            patientThread.start();

        }

        try {
            for (Thread patientThread : patientThreads) {
                patientThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            for (int i = 0; i < NumofPatient; i++) {
                treated.acquire();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (Thread doctorThread : doctorThreads) {
            doctorThread.interrupt();
        }

        try {
            for (Thread doctorThread : doctorThreads) {
                doctorThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("All patients have been treated.");
        System.out.println("Simulation completed.");
    }

    public static synchronized void logEvent(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        Hospital hospital = new Hospital();

        hospital.getUserInput();
        hospital.initializeHospital();
        hospital.startSimulation();
    }
}