public class Semaphore {

    private int permits;

    public Semaphore(int permits) {
        this.permits = permits;
    }

    public synchronized void acquire()
            throws InterruptedException {

        while (permits == 0) {
            wait();
        }

        permits--;
    }

    public synchronized void release() {
        permits++;
        notifyAll();
    }

    public synchronized boolean tryAcquire(long timeoutMillis)
            throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;

        while (permits == 0) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                return false;
            }
            wait(remaining);
        }
        permits--;
        return true;
    }
}