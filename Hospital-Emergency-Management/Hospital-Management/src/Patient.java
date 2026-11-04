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
        try {
            enterWaitingRoom();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Patient " + id + " was interrupted while waiting.");
        }
    }

    public void arrive() {
        Hospital.logEvent("P" + id + " arrived.");
    }

    public void enterWaitingRoom() throws InterruptedException {

        waitingRoom.addPatient(this);
    }

    public void leaveHospital(){

        Hospital.logEvent("P" + id + " left.");

    }

    public int getId() {
        return id;
    }
}
