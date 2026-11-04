import java.util.concurrent.ThreadLocalRandom;

public class Doctor implements Runnable {

    private final int id;
    private final WaitingRoom waitingRoom;
    private final TreatmentRooms treatmentRooms;

    public Doctor(int id, WaitingRoom waitingRoom, TreatmentRooms treatmentRooms) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
    }

    @Override
    public void run() {

        Hospital.logEvent("Doctor " + id + " is available.");

        while (!Thread.currentThread().isInterrupted()) {

            try {
                // Get the next patient
                Patient patient = takePatient();

                Hospital.logEvent(
                    "Doctor " + id + " received Patient " + patient.getId()
                );

                // Try to acquire a treatment room within 3 seconds
                boolean roomAcquired = treatmentRooms.tryAcquireRoom(3000);

                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                // If no room is available, return the patient
                if (!roomAcquired) {

                    Hospital.logEvent(
                        "Doctor " + id + " couldn't get a treatment room for Patient "
                        + patient.getId() + ". Returning patient to waiting room."
                    );

                    waitingRoom.addPatient(patient);

                    continue;
                }

                // Treat the patient
                try {
                    treatPatient(patient);
                } finally {
                    // Always release the room after successful acquisition
                    treatmentRooms.releaseRoom();

                    Hospital.logEvent(
                        "Doctor " + id + " released the treatment room."
                    );
                }

                // Patient leaves after treatment
                patient.leaveHospital();

                // Notify Hospital that one patient has completed treatment
                Hospital.treated.release();

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                Hospital.logEvent(
                    "Doctor " + id + " stopped working."
                );

                break;
            }
        }
    }

    private Patient takePatient() throws InterruptedException {
        return waitingRoom.getNextPatient();
    }

    private void treatPatient(Patient patient) throws InterruptedException {

        Hospital.logEvent(
            "Doctor " + id + " started treating Patient " + patient.getId()
        );

        // Random treatment duration between 1 and 5 seconds
        int treatmentTime = ThreadLocalRandom.current().nextInt(1000, 5001);

        Thread.sleep(treatmentTime);

        Hospital.logEvent(
            "Doctor " + id + " finished treating Patient " + patient.getId()
        );
    }
}
