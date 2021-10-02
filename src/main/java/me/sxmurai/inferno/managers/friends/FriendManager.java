package me.sxmurai.inferno.managers.friends;

import me.sxmurai.inferno.config.FriendsConfig;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.UUID;

public class FriendManager {
    private final ArrayList<Friend> friends = new ArrayList<>();
    private final FriendsConfig config;

    public FriendManager() {
        this.config = new FriendsConfig(this);
        this.config.load();
    }

    public void addFriend(Friend friend) {
        friends.add(friend);
    }

    public void addFriend(EntityPlayer player) {
        addFriend(new Friend(player.entityUniqueID));
    }

    public void removeFriend(Friend friend) {
        friends.remove(friend);
    }

    public void removeFriend(EntityPlayer player) {
        for (Friend friend : friends) {
            if (friend.getUuid().equals(player.entityUniqueID)) {
                friends.remove(friend);
                break;
            }
        }
    }

    public boolean isFriend(UUID uuid) {
        return friends.stream().anyMatch((friend) -> friend.getUuid().equals(uuid));
    }

    public boolean isFriend(EntityPlayer player) {
        return isFriend(player.entityUniqueID);
    }

    public Friend getFriend(EntityPlayer player) {
        for (Friend friend : this.friends) {
            if (friend.getUuid().equals(player.entityUniqueID)) {
                return friend;
            }
        }

        return null;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void unload() {
        this.config.stop();
    }
}
