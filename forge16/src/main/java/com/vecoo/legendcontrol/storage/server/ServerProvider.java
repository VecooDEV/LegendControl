package com.vecoo.legendcontrol.storage.server;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol.LegendControl;

public class ServerProvider {
    private final String filePath = UtilWorld.worldDirectory(LegendControl.getInstance().getConfig().getServerStorage());
    private ServerStorage serverStorage;

    public ServerStorage getServerStorage() {
        if (this.serverStorage == null) {
            new ServerStorage(LegendControl.getInstance().getConfig().getBaseChance(), "-");
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