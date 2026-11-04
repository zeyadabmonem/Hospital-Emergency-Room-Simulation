public class TreatmentRooms {
    

    private Semaphore roomSemaphore = new Semaphore(2);
    
    public boolean  tryAcquireRoom(long timeoutMillis) {
        try {
            
            return roomSemaphore.tryAcquire(timeoutMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void releaseRoom() {
        roomSemaphore.release();
    }
}
