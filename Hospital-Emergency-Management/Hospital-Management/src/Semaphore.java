public class Semaphore {

    private int permits;
    private final int maxPermits;

    public Semaphore(int permits) {
        this(permits, Integer.MAX_VALUE);
    }

    public Semaphore(int permits, int maxPermits) {
        if (permits < 0 || maxPermits < permits) {
            throw new IllegalArgumentException("Invalid semaphore permit count.");
        }
        this.permits = permits;
        this.maxPermits = maxPermits;
    }

    public synchronized void acquire()
            throws InterruptedException {

        while (permits == 0) {
            wait();
        }

        permits--;
    }

    public synchronized void release() {
        if (permits == maxPermits) {
            throw new IllegalStateException("Semaphore released more times than acquired.");
        }
        permits++;
        notifyAll();
    }

    public synchronized boolean tryAcquire(long timeoutMillis)
            throws InterruptedException {
        if (timeoutMillis < 0) {
            throw new IllegalArgumentException("Timeout cannot be negative.");
        }

        long deadline = System.nanoTime() + timeoutMillis * 1_000_000L;

        while (permits == 0) {
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0) {
                return false;
            }
            long remainingMillis = remaining / 1_000_000L;
            int remainingNanos = (int) (remaining % 1_000_000L);
            wait(remainingMillis, remainingNanos);
        }
        permits--;
        return true;
    }
}
