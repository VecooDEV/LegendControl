package com.vecoo.legendcontrol_defender.storage;

import com.google.gson.Gson;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

public class PlayerProvider {
    private transient final String filePath;
    private final Map<UUID, PlayerStorage> map;

    private transient boolean intervalStarted = false;

    public PlayerProvider(@Nonnull String filePath, @Nonnull MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);

        this.map = new HashMap<>();
    }

    @Nonnull
    public Map<UUID, PlayerStorage> getStorage() {
        return this.map;
    }

    @Nonnull
    public PlayerStorage getStorage(@Nonnull UUID playerUUID) {
        if (this.map.get(playerUUID) == null) {
            new PlayerStorage(playerUUID, new LinkedHashSet<>());
        }

        return this.map.get(playerUUID);
    }

    public void updatePlayerStorage(@Nonnull PlayerStorage storage) {
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
                        if (LegendControlDefender.getInstance().getServer().isServerRunning()) {
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