package com.vecoo.legendcontrol_defender.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Comment;
import lombok.Getter;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ServerConfig {
    @Comment("Maximum number of players in the trust list. Set to 0 to disable.")
    private int trustLimit = 15;
    @Comment("The number of seconds before the protection of a legendary pokemon is removed.")
    private int protectedTime = 300;
}