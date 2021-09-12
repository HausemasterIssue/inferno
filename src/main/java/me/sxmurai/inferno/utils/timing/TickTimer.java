package me.sxmurai.inferno.utils.timing;

import java.util.ArrayList;

public class TickTimer {
    private int ticks = 0;
    private final ArrayList<Listener> listeners = new ArrayList<>();

    public void reset() {
        this.ticks = 0;
    }

    public void tick() {
        ++this.ticks;
    }

    public boolean passed(int ticks) {
        if (this.ticks >= ticks) {
            listeners.forEach(Listener::run);
            return true;
        }

        return false;
    }

    public void listen(Listener listener) {
        this.listeners.add(listener);
    }

    @FunctionalInterface
    private interface Listener {
        void run();
    }
}
