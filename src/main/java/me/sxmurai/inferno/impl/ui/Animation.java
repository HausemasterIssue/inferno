package me.sxmurai.inferno.impl.ui;

import me.sxmurai.inferno.api.util.Timer;

public class Animation {
    private final Timer progressTimer = new Timer();
    private float progress = 0.0f;

    private final float max;
    private final float increment;
    private final long delay;
    private final boolean allowReversed;

    public Animation(float max, float increment, long delay, boolean allowReversed) {
        this.max = max;
        this.increment = increment;
        this.delay = delay;
        this.allowReversed = allowReversed;
    }

    public void update(boolean reverse) {
        if (this.progressTimer.passedMs(this.delay)) {
            this.progressTimer.reset();

            if (reverse && allowReversed) {
                if (this.progress != -this.max) {
                    this.progress -= this.increment;
                }
            } else {
                if (this.progress != this.max) {
                    this.progress += this.increment;
                }
            }
        }
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public float getProgress() {
        return progress;
    }
}
