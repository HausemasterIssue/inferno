package me.sxmurai.inferno.client.manager.managers.friends;

import java.util.UUID;

public class Friend {
    private final UUID uuid;
    private String alias = null;

    public Friend(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
