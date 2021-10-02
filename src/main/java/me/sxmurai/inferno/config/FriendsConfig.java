package me.sxmurai.inferno.config;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.managers.FileManager;
import me.sxmurai.inferno.managers.friends.Friend;
import me.sxmurai.inferno.managers.friends.FriendManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public class FriendsConfig extends BaseConfig {
    private final FriendManager manager;

    public FriendsConfig(FriendManager manager) {
        super(FileManager.getInstance().getClientFolder().resolve("friends.json"));
        this.manager = manager;
    }

    @Override
    public void save() {
        JSONArray json = new JSONArray();

        for (Friend friend : this.manager.getFriends()) {
            json.put(new JSONObject().put("uuid", friend.getUuid().toString()).put("alias", friend.getAlias()));
        }

        this.files.writeFile(this.path, json.toString(4));
    }

    @Override
    public void load() {
        String config = this.read();
        if (config == null || config.isEmpty()) {
            Inferno.LOGGER.info("Configuration not found, making a new one...");
            this.save();
            return;
        }

        for (Object object : new JSONArray(config).toList()) {
            if (!(object instanceof JSONObject)) {
                continue;
            }

            JSONObject json = (JSONObject) object;

            Friend friend = new Friend(UUID.fromString(this.get(json, "uuid", null)));
            if (friend.getUuid() == null) {
                continue;
            }

            friend.setAlias(this.get(json, "alias", null));

            this.manager.addFriend(friend);
        }
    }
}
