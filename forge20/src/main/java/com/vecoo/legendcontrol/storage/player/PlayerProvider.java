package com.vecoo.legendcontrol.storage.player;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol.LegendControl;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class PlayerProvider {
    private final String filePath = UtilWorld.worldDirectory(LegendControl.getInstance().getConfig().getPlayerStorage());
    private final HashMap<UUID, PlayerStorage> map;

    public PlayerProvider() {
        this.map = new HashMap<>();
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
        File dir = UtilGson.checkForDirectory(filePath);
        String[] list = dir.list();

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