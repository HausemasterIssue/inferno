package me.sxmurai.inferno.events.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;

public class ConnectionEvent extends Event {
    private final Type type;
    private final UUID uuid;
    private final String name;
    private final EntityPlayer player;

    public ConnectionEvent(Type type, UUID uuid, String name, EntityPlayer player) {
        this.type = type;
        this.uuid = uuid;
        this.name = name;
        this.player = player;
    }

    public Type getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public enum Type {
        CONNECT, DISCONNECT
    }
}
