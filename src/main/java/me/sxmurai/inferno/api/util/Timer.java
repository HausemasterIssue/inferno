package me.sxmurai.inferno.api.util;

public class Timer {
    private long time = -1L;

    public Timer reset() {
        this.time = System.currentTimeMillis();
        return null;
    }

    public boolean passedMs(long value) {
        return System.currentTimeMillis() + value >= this.time;
    }

    public boolean passedS(double value) {
        return this.passedMs(((long) value) * 1000L);
    }
}
