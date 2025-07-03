package com.vecoo.legendcontrol_defender.storage.player.impl;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.storage.player.PlayerProvider;
import com.vecoo.legendcontrol_defender.storage.player.PlayerStorage;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerJsonProvider implements PlayerProvider {
    private transient final String filePath;
    private final Map<UUID, PlayerStorage> map;

    public PlayerJsonProvider(String filePath, MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);

        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public Map<UUID, PlayerStorage> getMap() {
        return this.map;
    }

    @Override
    public PlayerStorage getPlayerStorage(UUID playerUUID) {
        if (this.map.get(playerUUID) == null) {
            new PlayerStorage(playerUUID, new HashSet<>());
        }

        return this.map.get(playerUUID);
    }

    @Override
    public void updatePlayerStorage(PlayerStorage storage) {
        this.map.put(storage.getUUID(), storage);

        write(storage).thenAccept(success -> {
            if (!success) {
                LegendControlDefender.getLogger().error("[LegendControl-Defender] Failed to write PlayerStorage.");
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> write(PlayerStorage storage) {
        return UtilGson.writeFileAsync(filePath, storage.getUUID() + ".json", UtilGson.newGson().toJson(storage));
    }

    @Override
    public void init() {
        String[] list = UtilGson.checkForDirectory(filePath).list();

        if (list == null) {
            return;
        }

        for (String file : list) {
            UtilGson.readFileAsync(filePath, file, el -> {
                PlayerStorage storage = UtilGson.newGson().fromJson(el, PlayerStorage.class);
                this.map.put(storage.getUUID(), storage);
            });
        }
    }
}