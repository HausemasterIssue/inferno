package me.sxmurai.inferno.utils.timing;

public class Timer {
    private long time = -1L;

    public boolean passedS(double s) {
        return this.passedMs((long)s * 1000L);
    }

    public boolean passedMs(long ms) {
        return this.passedNS(this.convertToNS(ms));
    }

    public boolean passedNS(long ns) {
        return System.nanoTime() - this.time >= ns;
    }

    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }

    public long convertToNS(long time) {
        return time * 1000000L;
    }
}