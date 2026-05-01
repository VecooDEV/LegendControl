package com.vecoo.legendcontrol_defender.service;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@ConfigSerializable
public class PlayerStorage {
    @NotNull
    @Setting("playerUUID")
    private final UUID playerUUID;
    @NotNull
    @Setting("playersTrust")
    private final Set<UUID> playersTrust;

    @Setter
    private transient volatile boolean dirty;

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
        return new PlayerStorage(this.playerUUID, new HashSet<>(this.playersTrust), false);
    }
}