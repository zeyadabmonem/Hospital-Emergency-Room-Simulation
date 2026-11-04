public class TreatmentRooms {
    private final Semaphore roomSemaphore = new Semaphore(2, 2);
    
    public boolean tryAcquireRoom(long timeoutMillis) throws InterruptedException {
        return roomSemaphore.tryAcquire(timeoutMillis);
    }

    public void releaseRoom() {
        roomSemaphore.release();
    }
}
