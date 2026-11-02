public class Doctor implements Runnable {
    private final int id;
    private final WaitingRoom waitingRoom;
    public Doctor(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }
    @Override
    public void run() {
        System.out.println("Doctor " + id + " is available.");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Patient patient = takePatient();
                System.out.println("Doctor [" + id + "] received Patient " + patient.getId());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(
                        "Doctor " + id + " stopped working."
                );
                break;
            }
        }
    }
    private Patient takePatient() throws InterruptedException {
        return waitingRoom.getNextPatient();
    }
}
