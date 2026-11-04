import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Hospital {
    private WaitingRoom waitingRoom;
    private TreatmentRooms treatmentRooms;
    private List<Doctor> doctors;
    private List<Patient> patients;
    private SimulationState simulationState;

    private int waitingRoomCapacity;
    private int numberOfPatients;
    private int numberOfDoctors;

    private List<Thread> doctorThreads;
    private List<Thread> patientThreads;

    public void getUserInput() {
        Scanner input = new Scanner(System.in);
        waitingRoomCapacity = readPositiveInt(input, "Enter waiting room capacity:");
        numberOfDoctors = readPositiveInt(input, "Enter number of doctors:");
        numberOfPatients = readNonNegativeInt(input, "Enter number of patients:");
    }

    private int readPositiveInt(Scanner input, String prompt) {
        while (true) {
            System.out.println(prompt);
            if (input.hasNextInt()) {
                int value = input.nextInt();
                if (value > 0) {
                    return value;
                }
            } else {
                input.next();
            }
            System.out.println("Please enter a positive integer.");
        }
    }

    private int readNonNegativeInt(Scanner input, String prompt) {
        while (true) {
            System.out.println(prompt);
            if (input.hasNextInt()) {
                int value = input.nextInt();
                if (value >= 0) {
                    return value;
                }
            } else {
                input.next();
            }
            System.out.println("Please enter zero or a positive integer.");
        }
    }

    public void initializeHospital() {
        waitingRoom = new WaitingRoom(waitingRoomCapacity);
        treatmentRooms = new TreatmentRooms();
        simulationState = new SimulationState();
        doctors = new ArrayList<>();
        patients = new ArrayList<>();
        doctorThreads = new ArrayList<>();
        patientThreads = new ArrayList<>();

        for (int i = 1; i <= numberOfDoctors; i++) {
            Doctor doctor = new Doctor(i, waitingRoom, treatmentRooms, simulationState);

            doctors.add(doctor);
            doctorThreads.add(new Thread(doctor));
        }
        for (int i = 1; i <= numberOfPatients; i++) {
            Patient patient = new Patient(i, waitingRoom, simulationState);

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

        if (numberOfPatients == 0) {
            System.out.println("All patients have been treated.");
            System.out.println("Simulation completed.");
            return;
        }

        for (Thread doctorThread : doctorThreads) {
            doctorThread.start();
        }
        for (Thread patientThread : patientThreads) {
            patientThread.start();
        }

        boolean completed = false;
        try {
            if (joinPatients()) {
                completed = simulationState.awaitAllPatients(numberOfPatients);
            }
        } catch (InterruptedException e) {
            simulationState.cancel();
            Thread.currentThread().interrupt();
        } finally {
            simulationState.requestShutdown();
            for (Thread patientThread : patientThreads) {
                patientThread.interrupt();
            }
            for (Thread doctorThread : doctorThreads) {
                doctorThread.interrupt();
            }
            joinDoctors();
        }

        if (completed) {
            System.out.println("All patients have been treated.");
            System.out.println("Simulation completed.");
        } else {
            System.out.println("Simulation cancelled before all patients were treated.");
        }
    }

    private boolean joinPatients() throws InterruptedException {
        for (Thread patientThread : patientThreads) {
            while (patientThread.isAlive()) {
                patientThread.join(100);
                if (simulationState.isCancelled()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void joinDoctors() {
        boolean interrupted = false;
        for (Thread doctorThread : doctorThreads) {
            while (true) {
                try {
                    doctorThread.join();
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
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
