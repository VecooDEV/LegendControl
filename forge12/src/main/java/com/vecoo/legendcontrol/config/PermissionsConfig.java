package com.vecoo.legendcontrol.config;

import com.google.gson.Gson;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.legendcontrol.LegendControl;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PermissionsConfig {
    private HashMap<String, Integer> permissionCommands;

    public PermissionsConfig() {
        this.permissionCommands = new HashMap<>();
        this.permissionCommands.put("minecraft.command.legendcontrol", 2);
        this.permissionCommands.put("minecraft.command.legendarytrust", 0);
        this.permissionCommands.put("minecraft.command.checklegendary", 0);
    }

    public HashMap<String, Integer> getPermissionCommand() {
        return this.permissionCommands;
    }

    private boolean write() {
        Gson gson = UtilGson.newGson();
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync("/config/LegendControl/", "permissions.json", gson.toJson(this));
        return future.join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/LegendControl/", "permissions.json", el -> {
                Gson gson = UtilGson.newGson();
                PermissionsConfig config = gson.fromJson(el, PermissionsConfig.class);

                this.permissionCommands.putAll(config.getPermissionCommand());
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            LegendControl.getLogger().error("Error in permissions config.");
        }
    }
}