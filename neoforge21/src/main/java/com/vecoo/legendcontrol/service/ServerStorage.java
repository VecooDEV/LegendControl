package com.vecoo.legendcontrol.service;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Setting;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
public class ServerStorage {
    @Setting("chanceLegend")
    private float chanceLegend;
    @NotNull
    @Setting("lastLegend")
    private String lastLegend;

    public void setChanceLegend(float amount) {
        this.chanceLegend = Math.min(100.0F, Math.max(amount, 0.0F));
    }

    @NotNull
    public ServerStorage copy() {
        return new ServerStorage(this.chanceLegend, this.lastLegend);
    }
}