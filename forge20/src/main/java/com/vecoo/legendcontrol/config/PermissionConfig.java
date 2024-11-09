package com.vecoo.legendcontrol.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

import java.util.HashMap;

@ConfigPath("config/LegendControl/permissions.yml")
@ConfigSerializable
public class PermissionConfig extends AbstractYamlConfig {
    private final HashMap<String, Integer> permissionCommand = new HashMap<>() {
        {
            this.put("minecraft.command.legendcontrol", 2);
            this.put("minecraft.command.legendarytrust", 0);
            this.put("minecraft.command.checklegends", 0);
        }
    };

    public HashMap<String, Integer> getPermissionCommand() {
        return this.permissionCommand;
    }
}