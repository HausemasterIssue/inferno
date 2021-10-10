package me.sxmurai.inferno.client.manager.managers.misc;

import me.sxmurai.inferno.client.manager.Manager;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

public class FileManager extends Manager {
    private static FileManager _INSTANCE;

    public Path getRoot() {
        return Paths.get("");
    }

    public Path getClientFolder() {
        return find(getRoot(), "inferno");
    }

    public Path find(Path base, String... paths) {
        return Paths.get(base.toString(), paths);
    }

    public boolean exists(Path path) {
        return Files.exists(path);
    }

    public String readFile(Path path) {
        try {
            return String.join("\n", Files.readAllLines(path, StandardCharsets.UTF_8));
        } catch (Exception exception) {
            return null;
        }
    }

    public void mkDir(Path path, boolean removeIfExists) {
        try {
            if (!Files.isDirectory(path)) {
                if (exists(path) && removeIfExists) {
                    Files.delete(path);
                }

                Files.createDirectories(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFile(Path path, String data) {
        try {
            Files.write(path, Collections.singletonList(data), StandardCharsets.UTF_8, exists(path) ? StandardOpenOption.WRITE : StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FileManager getInstance() {
        if (_INSTANCE == null) {
            return _INSTANCE = new FileManager();
        }

        return _INSTANCE;
    }
}