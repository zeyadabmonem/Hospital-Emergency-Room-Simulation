import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;

public class WaitingRoom {
    private final int capacity;
    private final Queue<Patient> patients;
    private final Set<Integer> queuedPatientIds;
    private final Semaphore emptySlots;
    private final Semaphore filledSlots;
    private final Semaphore mutex;

    public WaitingRoom(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Waiting room capacity must be positive.");
        }
        this.capacity = capacity;
        patients = new LinkedList<>();
        queuedPatientIds = new HashSet<>();
        emptySlots = new Semaphore(capacity, capacity);
        filledSlots = new Semaphore(0, capacity);
        mutex = new Semaphore(1, 1);
    }
    public  boolean isFull() throws InterruptedException{
        mutex.acquire();
        try{
            return patients.size()==capacity;
        }finally {
            mutex.release();
        }
        
    }
    public  boolean isEmpty() throws InterruptedException{
        mutex.acquire();
        try{
            return patients.size()==0;
        }finally {
            mutex.release();
        }
        
    }

    public void addPatient(Patient patient) throws InterruptedException  {
        if (!emptySlots.tryAcquire(0)) {
            Hospital.logEvent("The waiting room is full.");
            Hospital.logEvent("P" + patient.getId() + " is waiting outside for an available seat.");
            emptySlots.acquire();
        }

        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            emptySlots.release();
            throw e;
        }

        try {
            if (!queuedPatientIds.add(patient.getId())) {
                emptySlots.release();
                throw new IllegalStateException("Patient " + patient.getId() + " is already in the waiting room.");
            }
            patients.add(patient);
            Hospital.logEvent("P" + patient.getId() + " entered the waiting room.");
        } finally {
            mutex.release();
        }

        filledSlots.release();

    }

    public Patient getNextPatient() throws InterruptedException {
        filledSlots.acquire();
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            filledSlots.release();
            throw e;
        }

        Patient nextPatient;
        try {
            nextPatient = patients.poll();
            if (nextPatient == null) {
                throw new IllegalStateException("Waiting room semaphore and queue are inconsistent.");
            }
            queuedPatientIds.remove(nextPatient.getId());
        } finally {
            mutex.release();
        }

        emptySlots.release();
        return nextPatient;
    }
}
