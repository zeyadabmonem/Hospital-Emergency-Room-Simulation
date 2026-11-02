import java.util.LinkedList;
import java.util.Queue;
public class WaitingRoom {
    int capacity;
    Queue<Patient> patients;
    Semaphore emptyslots;
    Semaphore filledslots;
    Semaphore mutex;
    WaitingRoom(int capacity) {
        this.capacity = capacity;
        patients = new LinkedList<>();
        emptyslots=new Semaphore(capacity);
        filledslots=new Semaphore(0);
        mutex=new Semaphore(1);
    }

    public void addPatient(Patient patient) throws InterruptedException  {
        
            emptyslots.acquire();
            mutex.acquire();
        
        patients.add(patient);
        //System.out.println("p"+patient.getId+" entered the waiting room.");
        mutex.release();
        filledslots.release();


        
    }

    public Patient getNextPatient() throws InterruptedException {
        filledslots.acquire();
        mutex.acquire();
        Patient next_Patient=patients.remove();
        //System.out.println("p"+patient.getId+" left.");
        System.out.println( "left.");
        mutex.release();
        emptyslots.release();
        return  next_Patient;
       

}
}
