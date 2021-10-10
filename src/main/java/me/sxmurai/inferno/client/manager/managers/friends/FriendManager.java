package me.sxmurai.inferno.client.manager.managers.friends;

import me.sxmurai.inferno.client.config.FriendsConfig;
import me.sxmurai.inferno.client.manager.AbstractManager;
import me.sxmurai.inferno.client.manager.ConfigurableManager;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.UUID;

public class FriendManager extends ConfigurableManager<Friend> {
    public FriendManager() {
        this.configuration = new FriendsConfig(this);
    }

    @Override
    public void load() {
        this.configuration.load();
    }

    public void addFriend(Friend friend) {
        this.items.add(friend);
    }

    public void addFriend(EntityPlayer player) {
        addFriend(new Friend(player.entityUniqueID));
    }

    public void removeFriend(Friend friend) {
        this.items.remove(friend);
    }

    public void removeFriend(EntityPlayer player) {
        for (Friend friend : this.items) {
            if (friend.getUuid().equals(player.entityUniqueID)) {
                this.items.remove(friend);
                break;
            }
        }
    }

    public boolean isFriend(UUID uuid) {
        return this.items.stream().anyMatch((friend) -> friend.getUuid().equals(uuid));
    }

    public boolean isFriend(EntityPlayer player) {
        return isFriend(player.entityUniqueID);
    }

    public Friend getFriend(EntityPlayer player) {
        for (Friend friend : this.items) {
            if (friend.getUuid().equals(player.entityUniqueID)) {
                return friend;
            }
        }

        return null;
    }

    public ArrayList<Friend> getFriends() {
        return this.items;
    }

    public void unload() {
        this.configuration.stop();
    }
}
