package com.vecoo.legendcontrol.storage.player;

import com.vecoo.legendcontrol.LegendControl;

import java.util.List;
import java.util.UUID;

public class PlayerFactory {
    public static boolean hasPlayerTrust(UUID playerUUID, UUID targetUUID) {
        return LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getPlayersTrust().contains(targetUUID);
    }

    public static List<UUID> getPlayersTrust(UUID playerUUID) {
        return LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getPlayersTrust();
    }

    public static void addPlayerTrust(UUID playerUUID, UUID targetUUID) {
        LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).addPlayerTrust(targetUUID);
    }

    public static void removePlayerTrust(UUID playerUUID, UUID targetUUID) {
        LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).removePlayerTrust(targetUUID);
    }

    public static void removePlayersTrust(UUID playerUUID) {
        LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).removePlayersTrust();
    }
}