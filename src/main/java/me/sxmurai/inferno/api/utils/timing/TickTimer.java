package me.sxmurai.inferno.api.utils.timing;

public class TickTimer {
    private int ticks = 0;

    public TickTimer reset() {
        this.ticks = 0;
        return this;
    }

    public void tick() {
        ++this.ticks;
    }

    public boolean passed(int ticks) {
        return this.ticks >= ticks;
    }
}
