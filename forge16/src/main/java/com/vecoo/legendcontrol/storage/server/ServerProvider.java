package com.vecoo.legendcontrol.storage.server;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.server.MinecraftServer;

public class ServerProvider {
    private transient final String filePath;
    private ServerStorage serverStorage;

    public ServerProvider(String filePath, MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);
    }

    public ServerStorage getServerStorage() {
        if (this.serverStorage == null) {
            new ServerStorage(LegendControl.getInstance().getConfig().getBaseChance(), "None");
        }

        return this.serverStorage;
    }

    public void updateServerStorage(ServerStorage storage) {
        this.serverStorage = storage;

        if (!write(storage)) {
            getServerStorage();
        }
    }

    private boolean write(ServerStorage server) {
        return UtilGson.writeFileAsync(filePath, "ServerStorage.json", UtilGson.newGson().toJson(server)).join();
    }

    public void init() {
        UtilGson.readFileAsync(filePath, "ServerStorage.json", el -> this.serverStorage = UtilGson.newGson().fromJson(el, ServerStorage.class));
    }
}