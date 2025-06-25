package com.vecoo.legendcontrol.storage.server.impl;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerProvider;
import com.vecoo.legendcontrol.storage.server.ServerStorage;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CompletableFuture;

public class ServerJsonProvider implements ServerProvider {
    private transient final String filePath;
    private ServerStorage serverStorage;

    public ServerJsonProvider(String filePath, MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);
    }

    @Override
    public ServerStorage getServerStorage() {
        if (this.serverStorage == null) {
            this.serverStorage = new ServerStorage(LegendControl.getInstance().getConfig().getBaseChance(), "None");
        }

        return this.serverStorage;
    }

    @Override
    public void updateServerStorage(ServerStorage storage) {
        this.serverStorage = storage;

        write(storage).thenAccept(success -> {
            if (!success) {
                init();
                LegendControl.getLogger().error("[LegendControl] Failed to write ServerStorage, attempting reload...");
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> write(ServerStorage storage) {
        return UtilGson.writeFileAsync(filePath, "ServerStorage.json", UtilGson.newGson().toJson(storage));
    }

    @Override
    public void init() {
        UtilGson.readFileAsync(filePath, "ServerStorage.json", el -> this.serverStorage = UtilGson.newGson().fromJson(el, ServerStorage.class));
    }
}