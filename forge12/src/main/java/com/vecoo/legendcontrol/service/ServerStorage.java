package com.vecoo.legendcontrol.service;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Setting;
import lombok.*;

import javax.annotation.Nonnull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
public class ServerStorage {
    @Setting("chanceLegend")
    private float chanceLegend;
    @Nonnull
    @Setting("lastLegend")
    private String lastLegend;

    public void setChanceLegend(float amount) {
        this.chanceLegend = Math.min(100.0F, Math.max(amount, 0.0F));
    }

    @Nonnull
    public ServerStorage copy() {
        val storage = new ServerStorage();

        storage.chanceLegend = this.chanceLegend;
        storage.lastLegend = this.lastLegend;

        return storage;
    }
}