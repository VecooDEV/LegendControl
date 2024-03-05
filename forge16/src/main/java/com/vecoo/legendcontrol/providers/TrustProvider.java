package com.vecoo.legendcontrol.providers;

import com.google.gson.Gson;
import com.vecoo.extraapi.gson.UtilGson;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TrustProvider {
    private String filePath = "/config/players/LegendControl/trust/";
    private HashMap<UUID, PlayerTrust> trustMap;

    public TrustProvider() {
        trustMap = new HashMap<>();
    }

    public PlayerTrust getPlayerTrust(UUID player) {
        if (trustMap.get(player) == null) {
            new PlayerTrust(player);
        }
        return trustMap.get(player);
    }

    public boolean getKey(UUID player) {
        if (trustMap.get(player) == null) {
            new PlayerTrust(player);
        }
        return trustMap.containsKey(player);
    }

    public void updatePlayerTrust(PlayerTrust playerHistory) {
        trustMap.put(playerHistory.getPlayer(), playerHistory);
        if (!write(playerHistory)) {
            UUID uuid = playerHistory.getPlayer();
            new PlayerTrust(uuid);
        }
    }

    private boolean write(PlayerTrust player) {
        Gson gson = UtilGson.newGson();

        CompletableFuture<Boolean> future = UtilGson.writeFileAsync(filePath, player.getPlayer() + ".json",
                gson.toJson(player));
        return future.join();
    }

    public void init() {
        File dir = UtilGson.checkForDirectory(filePath);
        String[] list = dir.list();

        if (list.length == 0) {
            return;
        }

        for (String file : list) {
            UtilGson.readFileAsync(filePath, file, el -> {
                Gson gson = UtilGson.newGson();
                PlayerTrust player = gson.fromJson(el, PlayerTrust.class);
                trustMap.put(player.getPlayer(), player);
            });
        }
    }
}