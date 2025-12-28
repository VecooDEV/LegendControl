package com.vecoo.legendcontrol_defender.service;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import lombok.Getter;
import lombok.val;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerService {
    @NotNull
    private transient final String filePath;
    @NotNull
    private final Map<UUID, PlayerStorage> storage;

    public PlayerService(@NotNull String filePath, @NotNull MinecraftServer server) {
        this.filePath = UtilWorld.resolveWorldDirectory(filePath, server);

        this.storage = new HashMap<>();
    }

    @NotNull
    public PlayerStorage getStorage(@NotNull UUID playerUUID) {
        if (this.storage.get(playerUUID) == null) {
            new PlayerStorage(playerUUID, new LinkedHashSet<>());
        }

        return this.storage.get(playerUUID);
    }

    public void updatePlayerStorage(@NotNull PlayerStorage storage) {
        storage.setDirty(true);
        this.storage.put(storage.getPlayerUUID(), storage);
    }

    public void save() {
        for (PlayerStorage storage : this.storage.values()) {
            UtilGson.writeFileAsync(this.filePath, storage.getPlayerUUID() + ".json",
                    UtilGson.getGson().toJson(storage)).join();
        }
    }

    private void saveInterval() {
        TaskTimer.builder()
                .withoutDelay()
                .interval(350 * 20L)
                .infinite()
                .consume(task -> {
                    if (LegendControlDefender.getInstance().getServer().isRunning()) {
                        for (PlayerStorage storage : this.storage.values()) {
                            if (storage.isDirty()) {
                                UtilGson.writeFileAsync(this.filePath, storage.getPlayerUUID() + ".json",
                                        UtilGson.getGson().toJson(storage)).thenRun(() -> storage.setDirty(false));
                            }
                        }
                    }
                })
                .build();
    }

    public void init() {
        val list = UtilGson.checkForDirectory(this.filePath).list();

        if (list == null) {
            return;
        }

        for (String file : list) {
            UtilGson.readFileAsync(this.filePath, file, el -> {
                val storage = UtilGson.getGson().fromJson(el, PlayerStorage.class);

                storage.setDirty(false);
                this.storage.put(storage.getPlayerUUID(), storage);
            }).join();
        }

        saveInterval();
    }
}