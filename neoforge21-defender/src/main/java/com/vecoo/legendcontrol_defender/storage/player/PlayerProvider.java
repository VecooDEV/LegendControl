package com.vecoo.legendcontrol_defender.storage.player;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerProvider {
    private transient final String filePath;
    private final ConcurrentHashMap<UUID, PlayerStorage> map;

    public PlayerProvider(String filePath, MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);

        this.map = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<UUID, PlayerStorage> getMap() {
        return this.map;
    }

    public PlayerStorage getPlayerStorage(UUID playerUUID) {
        if (this.map.get(playerUUID) == null) {
            new PlayerStorage(playerUUID);
        }

        return this.map.get(playerUUID);
    }

    public void updatePlayerStorage(PlayerStorage storage) {
        this.map.put(storage.getUUID(), storage);

        write(storage).thenAccept(success -> {
            if (!success) {
                LegendControlDefender.getLogger().error("[LegendControl-Defender] Failed to write PlayerStorage.");
            }
        });
    }

    private CompletableFuture<Boolean> write(PlayerStorage storage) {
        return UtilGson.writeFileAsync(filePath, storage.getUUID() + ".json", UtilGson.newGson().toJson(storage));
    }

    public void init() {
        String[] list = UtilGson.checkForDirectory(filePath).list();

        if (list == null) {
            return;
        }

        for (String file : list) {
            UtilGson.readFileAsync(filePath, file, el -> {
                PlayerStorage player = UtilGson.newGson().fromJson(el, PlayerStorage.class);
                this.map.put(player.getUUID(), player);
            });
        }
    }
}