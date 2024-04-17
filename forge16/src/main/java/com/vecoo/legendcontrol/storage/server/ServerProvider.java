package com.vecoo.legendcontrol.storage.server;

import com.google.gson.Gson;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.util.UtilGson;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ServerProvider {
    private String filePath = "/config/server/LegendControl/";
    private HashMap<String, ServerStorage> map;

    public ServerProvider() {
        this.map = new HashMap<>();
    }

    public ServerStorage getServerStorage(String storage) {
        if (this.map.get(storage) == null) {
            new ServerStorage(LegendControl.getInstance().getConfig().getBaseChance(), "-");
        }
        return this.map.get(storage);
    }

    public void updateServerStorage(ServerStorage storage) {
        for (String name : storage.getStorageName()) {
            this.map.put(name, storage);
            if (!write(storage)) {
                getServerStorage(name);
            }
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
            ServerStorage storage = gson.fromJson(el, ServerStorage.class);
            for (String name : storage.getStorageName()) {
                this.map.put(name, storage);
            }
        });
    }
}