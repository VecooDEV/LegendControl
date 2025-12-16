package com.vecoo.legendcontrol_defender.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.Getter;

@Getter
@ConfigSerializable
public class ServerConfig {
    private int trustLimit = 15;
    private int protectedTime = 300;
}