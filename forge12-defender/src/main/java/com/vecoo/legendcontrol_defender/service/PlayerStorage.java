package com.vecoo.legendcontrol_defender.service;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.annotation.Nonnull;
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
    @Nonnull
    @Setting("playerUUID")
    private final UUID playerUUID;
    @Nonnull
    @Setting("playersTrust")
    private final Set<UUID> playersTrust;

    @Nonnull
    private transient final AtomicBoolean dirty = new AtomicBoolean(true);

    public void addPlayerTrust(@Nonnull UUID playerUUID) {
        this.playersTrust.add(playerUUID);
    }

    public void removePlayerTrust(@Nonnull UUID playerUUID) {
        this.playersTrust.remove(playerUUID);
    }

    public void clearPlayersTrust() {
        this.playersTrust.clear();
    }

    @Nonnull
    public PlayerStorage copy() {
        return new PlayerStorage(this.playerUUID, new LinkedHashSet<>(this.playersTrust));
    }
}