package com.vecoo.legendcontrol.config;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.legendcontrol.LegendControl;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PermissionConfig {
    private HashMap<String, Integer> permissionCommands;

    public PermissionConfig() {
        this.permissionCommands = new HashMap<>();
        this.permissionCommands.put("minecraft.command.legendcontrol", 2);
        this.permissionCommands.put("minecraft.command.legendarytrust", 0);
        this.permissionCommands.put("minecraft.command.checklegends", 0);
    }

    public HashMap<String, Integer> getPermissionCommand() {
        return this.permissionCommands;
    }

    private void write() {
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync("/config/LegendControl/", "permission.json", UtilGson.newGson().toJson(this));
        future.join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/LegendControl/", "permission.json", el -> {
                PermissionConfig config = UtilGson.newGson().fromJson(el, PermissionConfig.class);

                this.permissionCommands = config.getPermissionCommand();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            LegendControl.getLogger().error("[LegendControl] Error in permission config.");
            write();
        }
    }
}