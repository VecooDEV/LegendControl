package com.vecoo.legendcontrol_defender.service;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ConfigSerializable
public class PlayerStorage {
    @NotNull
    @Setting("playerUUID")
    private final UUID playerUUID;
    @NotNull
    @Setting("playersTrust")
    private final Set<UUID> playersTrust;

    @NotNull
    private transient final AtomicBoolean dirty = new AtomicBoolean(true);

    public void addPlayerTrust(@NotNull UUID playerUUID) {
        this.playersTrust.add(playerUUID);
    }

    public void removePlayerTrust(@NotNull UUID playerUUID) {
        this.playersTrust.remove(playerUUID);
    }

    public void clearPlayersTrust() {
        this.playersTrust.clear();
    }

    @NotNull
    public PlayerStorage copy() {
        return new PlayerStorage(this.playerUUID, new LinkedHashSet<>(this.playersTrust));
    }
}