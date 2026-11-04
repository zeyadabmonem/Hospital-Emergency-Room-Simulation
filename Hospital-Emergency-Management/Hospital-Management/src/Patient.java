public class Patient implements Runnable {
    private final int id;
    private final WaitingRoom waitingRoom;
    private final SimulationState simulationState;

    public Patient(int id, WaitingRoom waitingRoom, SimulationState simulationState) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.simulationState = simulationState;
    }
    @Override 
    public void run() {

        arrive();
        try {
            enterWaitingRoom();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            simulationState.cancel();
            Hospital.logEvent("P" + id + " was interrupted while waiting.");
        }
    }

    public void arrive() {
        Hospital.logEvent("P" + id + " arrived.");
    }

    public void enterWaitingRoom() throws InterruptedException {

        waitingRoom.addPatient(this);
    }

    public void leaveHospital() {

        Hospital.logEvent("P" + id + " left.");

    }

    public int getId() {
        return id;
    }
}
