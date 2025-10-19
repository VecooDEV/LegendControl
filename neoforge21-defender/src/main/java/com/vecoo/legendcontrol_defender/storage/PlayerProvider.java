package com.vecoo.legendcontrol_defender.storage;

import com.google.gson.Gson;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

public class PlayerProvider {
    private transient final String filePath;
    private final Map<UUID, PlayerStorage> map;

    private transient boolean intervalStarted = false;

    public PlayerProvider(@NotNull String filePath, @NotNull MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);

        this.map = new HashMap<>();
    }

    public Map<UUID, PlayerStorage> getStorage() {
        return this.map;
    }

    @NotNull
    public PlayerStorage getStorage(@NotNull UUID playerUUID) {
        if (this.map.get(playerUUID) == null) {
            new PlayerStorage(playerUUID, new LinkedHashSet<>());
        }

        return this.map.get(playerUUID);
    }

    public void updatePlayerStorage(@NotNull PlayerStorage storage) {
        storage.setDirty(true);
        this.map.put(storage.getPlayerUUID(), storage);
    }

    public void write() {
        Gson gson = UtilGson.newGson();

        for (PlayerStorage storage : this.map.values()) {
            UtilGson.writeFileAsync(this.filePath, storage.getPlayerUUID() + ".json", gson.toJson(storage)).join();
        }
    }

    private void writeInterval() {
        if (!this.intervalStarted) {
            TaskTimer.builder()
                    .withoutDelay()
                    .interval(300 * 20L)
                    .infinite()
                    .consume(task -> {
                        if (LegendControlDefender.getInstance().getServer().isRunning()) {
                            Gson gson = UtilGson.newGson();

                            for (PlayerStorage storage : this.map.values()) {
                                if (storage.isDirty()) {
                                    UtilGson.writeFileAsync(this.filePath, storage.getPlayerUUID() + ".json",
                                            gson.toJson(storage)).thenRun(() -> storage.setDirty(false));
                                }
                            }
                        }
                    })
                    .build();

            this.intervalStarted = true;
        }
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