package com.vecoo.legendcontrol.providers;

import com.google.gson.Gson;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.util.Utils;

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
        if (chanceMap.get("legendaryChance") == null) {
            new LegendaryChance("legendaryChance", LegendControl.getInstance().getConfig().getBaseChance());
        }
        return chanceMap.get("legendaryChance");
    }

    public void updateLegendaryChance(LegendaryChance chance) {
        chanceMap.put("legendaryChance", chance);
        if (!write(chance)) {
            getLegendaryChance();
        }
    }

    private boolean write(LegendaryChance player) {
        Gson gson = Utils.newGson();
        CompletableFuture<Boolean> future = Utils.writeFileAsync(filePath, "LegendaryChance" + ".json",
                gson.toJson(player));
        return future.join();
    }

    public void init() {
        File dir = Utils.checkForDirectory(filePath);
        String[] list = dir.list();
        if (list.length == 0) {
            return;
        }
        for (String file : list) {
            Utils.readFileAsync(filePath, file, el -> {
                Gson gson = Utils.newGson();
                LegendaryChance chance = gson.fromJson(el, LegendaryChance.class);
                chanceMap.put("legendaryChance", chance);
            });
        }
    }
}
