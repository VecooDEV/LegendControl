package com.vecoo.legendcontrol_defender.storage.player;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.world.UtilWorld;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProvider {
    private transient final String filePath;
    private final Map<UUID, PlayerStorage> map = new HashMap<>();

    public PlayerProvider(String filePath, MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);
    }

    public PlayerStorage getPlayerStorage(UUID playerUUID) {
        if (this.map.get(playerUUID) == null) {
            new PlayerStorage(playerUUID);
        }
        return this.map.get(playerUUID);
    }

    public void updatePlayerStorage(PlayerStorage player) {
        this.map.put(player.getUuid(), player);
        if (!write(player)) {
            getPlayerStorage(player.getUuid());
        }
    }

    private boolean write(PlayerStorage player) {
        return UtilGson.writeFileAsync(filePath, player.getUuid() + ".json", UtilGson.newGson().toJson(player)).join();
    }

    public void init() {
        String[] list = UtilGson.checkForDirectory(filePath).list();

        if (list == null) {
            return;
        }

        for (String file : list) {
            UtilGson.readFileAsync(filePath, file, el -> {
                PlayerStorage player = UtilGson.newGson().fromJson(el, PlayerStorage.class);
                this.map.put(player.getUuid(), player);
            });
        }
    }
}