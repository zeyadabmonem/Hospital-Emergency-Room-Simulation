import java.util.concurrent.ThreadLocalRandom;

public class Doctor implements Runnable {

    private final int id;
    private final WaitingRoom waitingRoom;
    private final TreatmentRooms treatmentRooms;
    private final SimulationState simulationState;

    public Doctor(int id, WaitingRoom waitingRoom, TreatmentRooms treatmentRooms,
                  SimulationState simulationState) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
        this.simulationState = simulationState;
    }

    @Override
    public void run() {

        Hospital.logEvent("Doctor " + id + " is available.");

        Patient patient = null;
        while (!Thread.currentThread().isInterrupted() && !simulationState.isCancelled()) {

            try {
                patient = takePatient();

                Hospital.logEvent(
                    "Doctor " + id + " is assigned to P" + patient.getId()
                );

                Hospital.logEvent("Doctor " + id + " is trying to acquire a treatment room.");
                boolean roomAcquired = treatmentRooms.tryAcquireRoom(3000);

                if (!roomAcquired) {
                    Hospital.logEvent(
                        "Doctor " + id + " could not acquire a treatment room."
                    );
                    Hospital.logEvent("Doctor " + id + " is sending P" + patient.getId()
                        + " back to the waiting room.");
                    waitingRoom.addPatient(patient);
                    patient = null;
                    Hospital.logEvent("Doctor " + id + " is now available.");

                    continue;
                }

                try {
                    Hospital.logEvent("Doctor " + id + " acquired a treatment room.");
                    treatPatient(patient);
                } finally {
                    treatmentRooms.releaseRoom();
                    Hospital.logEvent("Doctor " + id + " released the treatment room.");
                }

                patient.leaveHospital();
                simulationState.patientTreated();
                patient = null;
                Hospital.logEvent("Doctor " + id + " is now available.");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (!simulationState.isShutdownRequested()) {
                    simulationState.cancel();
                    if (patient != null) {
                        Hospital.logEvent("Doctor " + id + " was interrupted while handling P"
                            + patient.getId() + ". Simulation cancelled.");
                    }
                }
                Hospital.logEvent("Doctor " + id + " stopped working.");

                break;
            }
        }
    }

    private Patient takePatient() throws InterruptedException {
        return waitingRoom.getNextPatient();
    }

    private void treatPatient(Patient patient) throws InterruptedException {

        Hospital.logEvent(
            "Doctor " + id + " begins treating P" + patient.getId()
        );

        // Random treatment duration between 1 and 5 seconds
        int treatmentTime = ThreadLocalRandom.current().nextInt(1000, 5001);

        Thread.sleep(treatmentTime);

        Hospital.logEvent(
            "Doctor " + id + " finished treating P" + patient.getId()
        );
    }
}
