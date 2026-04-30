package com.vecoo.legendcontrol.service;

import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServerStorage {
    private float chanceLegend;
    @NotNull
    private String lastLegend;

    public void setChanceLegend(float amount) {
        this.chanceLegend = Math.min(100.0F, Math.max(amount, 0.0F));
    }

    @NotNull
    public ServerStorage copy() {
        return new ServerStorage(this.chanceLegend, this.lastLegend);
    }
}