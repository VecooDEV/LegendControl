package com.vecoo.legendcontrol.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

import java.util.HashMap;

@ConfigPath("config/LegendControl/permissions.yml")
@ConfigSerializable
public class PermissionsConfig extends AbstractYamlConfig {
    private HashMap<String, Integer> permissions = new HashMap<>() {
        {
            this.put("minecraft.command.legendcontrol", 2);
            this.put("minecraft.command.legendarytrust", 0);
            this.put("minecraft.command.checklegendary", 0);
        }
    };

    public HashMap<String, Integer> getPermissions() {
        return this.permissions;
    }
}