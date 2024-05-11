package com.vecoo.legendcontrol.storage.server;

import com.google.gson.Gson;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.util.UtilGson;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ServerProvider {
    private String filePath = "/config/server/LegendControl/";
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
        Gson gson = UtilGson.newGson();
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync(filePath, "ServerStorage.json", gson.toJson(server));
        return future.join();
    }

    public void init() {
        UtilGson.readFileAsync(filePath, "ServerStorage.json", el -> {
            Gson gson = UtilGson.newGson();
            this.serverStorage = gson.fromJson(el, ServerStorage.class);
        });
    }
}