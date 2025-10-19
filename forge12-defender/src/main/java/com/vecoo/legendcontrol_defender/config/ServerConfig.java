package com.vecoo.legendcontrol_defender.config;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.legendcontrol_defender.LegendControlDefender;

public class ServerConfig {
    private int trustLimit = 15;
    private int protectedTime = 300;

    public int getTrustLimit() {
        return this.trustLimit;
    }

    public int getProtectedTime() {
        return this.protectedTime;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/LegendControl/Defender/", "config.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        boolean completed = UtilGson.readFileAsync("/config/LegendControl/Defender/", "config.json", el -> {
            ServerConfig config = UtilGson.newGson().fromJson(el, ServerConfig.class);

            this.protectedTime = config.getProtectedTime();
            this.trustLimit = config.getTrustLimit();
        }).join();

        if (!completed) {
            LegendControlDefender.getLogger().error("[LegendControl-Defender] Error in config.");
            write();
        }
    }
}