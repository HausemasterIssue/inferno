package me.sxmurai.inferno.client.config;

import me.sxmurai.inferno.client.manager.managers.misc.FileManager;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class BaseConfig {
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    protected final Path path;
    protected final FileManager files = FileManager.getInstance();

    public BaseConfig(Path path) {
        this.path = path;
        this.service.scheduleAtFixedRate(this::save, 5, 5, TimeUnit.MINUTES);
    }

    public abstract void save();
    public abstract void load();

    public void stop() {
        this.service.shutdown();
        this.save();
    }

    protected String read() {
        return files.readFile(this.path);
    }

    protected <T> T get(JSONObject object, String key) {
        return get(object, key, null);
    }

    protected <T> T get(JSONObject object, String key, T defaultValue) {
        return object.has(key) ? (T) object.get(key) : defaultValue;
    }
}
