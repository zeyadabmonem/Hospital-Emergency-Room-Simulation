public class Patient implements Runnable {
    private int id;
    private WaitingRoom waitingRoom;

    public Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }
    @Override 
    public void run() {

        arrive();
        enterWaitingRoom();
    }

    public void arrive() {
        Hospital.logEvent("p" + id + " is arrived.");
    }

    public void enterWaitingRoom() {
        
        waitingRoom.addPatient(this);
    }

    public void leaveHospital(){

        Hospital.logEvent("p"+id+" is lefte.");

    }

    public int getId() {
        return id;
    }
}
