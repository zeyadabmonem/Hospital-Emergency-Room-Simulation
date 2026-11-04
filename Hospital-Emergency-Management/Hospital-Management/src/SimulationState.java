/** Coordinates normal completion and cancellation of one simulation run. */
public class SimulationState {
    private final Semaphore treatedPatients = new Semaphore(0);
    private boolean cancelled;
    private boolean shutdownRequested;

    public synchronized void patientTreated() {
        treatedPatients.release();
    }

    public synchronized void cancel() {
        cancelled = true;
    }

    public synchronized void requestShutdown() {
        shutdownRequested = true;
    }

    public synchronized boolean isCancelled() {
        return cancelled;
    }

    public synchronized boolean isShutdownRequested() {
        return shutdownRequested;
    }

    /**
     * Waits for every patient to finish, unless another thread cancels the run.
     */
    public boolean awaitAllPatients(int patientCount) throws InterruptedException {
        for (int i = 0; i < patientCount; i++) {
            while (!treatedPatients.tryAcquire(100)) {
                if (isCancelled()) {
                    return false;
                }
            }
        }
        return !isCancelled();
    }
}
