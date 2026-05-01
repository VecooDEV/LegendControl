package com.vecoo.legendcontrol_defender.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Comment;
import lombok.Getter;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class PermissionConfig {
    @Comment("Will the /legendarytrust command be available to all players? It doesn't work with LuckPerms. A restart is required.")
    private boolean isLegendaryTrustCommand = true;
}
