package com.vecoo.legendcontrol.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Comment;
import lombok.Getter;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class PermissionConfig {
    @Comment("Will the /checklegendary command be available to all players? It doesn't work with LuckPerms.")
    private boolean isCheckLegendaryCommand = true;
}
