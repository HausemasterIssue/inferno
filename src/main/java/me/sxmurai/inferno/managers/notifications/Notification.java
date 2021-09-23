package me.sxmurai.inferno.managers.notifications;

import me.sxmurai.inferno.utils.timing.Timer;

public class Notification {
    private String text;
    private final int id;

    private long time = -1L;
    private final Timer timer = new Timer();

    public Notification(String text, int id) {
        this.text = text;
        this.id = id;
    }

    public Notification(String text, int id, long time) {
        this(text, id);
        this.time = time;
        this.timer.reset();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public boolean shouldDelete() {
        return time != -1L && this.timer.passedMs(this.time);
    }
}
