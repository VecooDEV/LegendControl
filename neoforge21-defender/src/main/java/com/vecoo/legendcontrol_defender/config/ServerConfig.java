package com.vecoo.legendcontrol_defender.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

@ConfigPath("config/LegendControl/Defender/config.yml")
@ConfigSerializable
public class ServerConfig extends AbstractYamlConfig {
    private int trustLimit = 15;
    private int protectedTime = 300;

    public int getTrustLimit() {
        return this.trustLimit;
    }

    public int getProtectedTime() {
        return this.protectedTime;
    }
}