package com.vecoo.legendcontrol_defender.storage;

import com.google.gson.Gson;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class PlayerProvider {
    private transient final String filePath;
    private final Map<UUID, PlayerStorage> map;

    public PlayerProvider(String filePath, MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);

        this.map = new HashMap<>();
    }

    public Map<UUID, PlayerStorage> getMap() {
        return this.map;
    }

    public PlayerStorage getPlayerStorage(UUID playerUUID) {
        if (this.map.get(playerUUID) == null) {
            new PlayerStorage(playerUUID, new HashSet<>());
        }

        return this.map.get(playerUUID);
    }

    public void updatePlayerStorage(PlayerStorage storage) {
        storage.setDirty(true);
        this.map.put(storage.getPlayerUUID(), storage);
    }

    public void write() {
        Gson gson = UtilGson.newGson();

        for (PlayerStorage storage : this.map.values()) {
            UtilGson.writeFileAsync(this.filePath, storage.getPlayerUUID() + ".json", gson.toJson(getPlayerStorage(storage.getPlayerUUID()))).join();
        }
    }

    private void writeInterval() {
        TaskTimer.builder()
                .withoutDelay()
                .interval(180 * 20L)
                .infinite()
                .consume(task -> {
                    if (LegendControlDefender.getInstance().getServer().isRunning()) {
                        Gson gson = UtilGson.newGson();

                        for (PlayerStorage storage : this.map.values()) {
                            if (storage.isDirty()) {
                                UtilGson.writeFileAsync(this.filePath, storage.getPlayerUUID() + ".json", gson.toJson(storage)).thenRun(() -> storage.setDirty(false));
                            }
                        }
                    }
                })
                .build();
    }

    public void init() {
        String[] list = UtilGson.checkForDirectory(this.filePath).list();

        if (list == null) {
            return;
        }

        for (String file : list) {
            UtilGson.readFileAsync(this.filePath, file, el -> {
                PlayerStorage storage = UtilGson.newGson().fromJson(el, PlayerStorage.class);
                storage.setDirty(false);
                this.map.put(storage.getPlayerUUID(), storage);
            }).join();
        }

        writeInterval();
    }
}