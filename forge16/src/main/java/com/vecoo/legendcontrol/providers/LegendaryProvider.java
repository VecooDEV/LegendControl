package com.vecoo.legendcontrol.providers;

import com.google.gson.Gson;
import com.vecoo.extraapi.gson.UtilGson;
import com.vecoo.legendcontrol.LegendControl;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class LegendaryProvider {
    private String filePath = "/config/temp/LegendControl/";
    private HashMap<String, LegendaryChance> chanceMap;

    public LegendaryProvider() {
        chanceMap = new HashMap<>();
    }

    public LegendaryChance getLegendaryChance() {
        if (chanceMap.get("chance") == null) {
            new LegendaryChance(LegendControl.getInstance().getConfig().getBaseChance());
        }
        return chanceMap.get("chance");
    }

    public void updateLegendaryChance(LegendaryChance chance) {
        chanceMap.put("chance", chance);
        if (!write(chance)) {
            getLegendaryChance();
        }
    }

    private boolean write(LegendaryChance player) {
        Gson gson = UtilGson.newGson();
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync(filePath, "LegendaryChance" + ".json",
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
                LegendaryChance chance = gson.fromJson(el, LegendaryChance.class);
                chanceMap.put("chance", chance);
            });
        }
    }
}