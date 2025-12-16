package com.vecoo.legendcontrol_defender.service;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import lombok.val;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

public class PlayerService {
    private transient final String filePath;
    private final Map<UUID, PlayerStorage> playerMap;

    public PlayerService(@NotNull String filePath, @NotNull MinecraftServer server) {
        this.filePath = UtilWorld.resolveWorldDirectory(filePath, server);

        this.playerMap = new HashMap<>();
    }

    @NotNull
    public Map<UUID, PlayerStorage> getStorage() {
        return this.playerMap;
    }

    @NotNull
    public PlayerStorage getStorage(@NotNull UUID playerUUID) {
        if (this.playerMap.get(playerUUID) == null) {
            new PlayerStorage(playerUUID, new LinkedHashSet<>());
        }

        return this.playerMap.get(playerUUID);
    }

    public void updatePlayerStorage(@NotNull PlayerStorage playerStorage) {
        playerStorage.setDirty(true);
        this.playerMap.put(playerStorage.getPlayerUUID(), playerStorage);
    }

    public void save() {
        for (PlayerStorage playerStorage : this.playerMap.values()) {
            UtilGson.writeFileAsync(this.filePath, playerStorage.getPlayerUUID() + ".json",
                    UtilGson.getGson().toJson(playerStorage)).join();
        }
    }

    private void saveInterval() {
        TaskTimer.builder()
                .withoutDelay()
                .interval(350 * 20L)
                .infinite()
                .consume(task -> {
                    if (LegendControlDefender.getInstance().getServer().isRunning()) {
                        for (PlayerStorage playerStorage : this.playerMap.values()) {
                            if (playerStorage.isDirty()) {
                                UtilGson.writeFileAsync(this.filePath, playerStorage.getPlayerUUID() + ".json",
                                        UtilGson.getGson().toJson(playerStorage)).thenRun(() -> playerStorage.setDirty(false));
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
                val playerStorage = UtilGson.getGson().fromJson(el, PlayerStorage.class);

                playerStorage.setDirty(false);
                this.playerMap.put(playerStorage.getPlayerUUID(), playerStorage);
            }).join();
        }

        saveInterval();
    }
}