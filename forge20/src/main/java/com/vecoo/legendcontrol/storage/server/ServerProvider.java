package com.vecoo.legendcontrol.storage.server;

import com.google.gson.Gson;
import com.vecoo.extrasapi.gson.UtilGson;
import com.vecoo.legendcontrol.LegendControl;

import java.util.concurrent.CompletableFuture;

public class ServerProvider {
    private final String filePath = LegendControl.PATH + "/storage/LegendControl/server/";
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