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
        
            emptyslots.acquire();
            try{
                mutex.acquire();
            }
            catch (InterruptedException e) {
                emptyslots.release();   
                throw e;
            }
            
        try{
            patients.add(patient);
            System.out.println("p"+patient.getId()+" entered the waiting room.");
        }
        finally{
            mutex.release();
        }

        filledslots.release();

    }

    public Patient getNextPatient() throws InterruptedException {
        filledslots.acquire();
        try{
            mutex.acquire();
        }
        catch(InterruptedException e){
            filledslots.release();
            throw e;
        }
        Patient next_Patient;
        try{
         next_Patient=patients.poll();
        }
        finally{
            mutex.release();
        }
        
        emptyslots.release();
        return  next_Patient;
       
    }
}
