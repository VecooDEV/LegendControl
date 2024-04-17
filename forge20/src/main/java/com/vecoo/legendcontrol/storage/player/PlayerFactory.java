package com.vecoo.legendcontrol.storage.player;

import com.vecoo.legendcontrol.LegendControl;

import java.util.List;
import java.util.UUID;

public class PlayerFactory {
    public static boolean hasPlayer(UUID playerUUID, UUID player) {
        return LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getPlayersUUID().contains(player);
    }

    public static List<UUID> getPlayers(UUID playerUUID) {
        return LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getPlayersUUID();
    }

    public static void addPlayer(UUID playerUUID, UUID player) {
        LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).addPlayersUUID(player);
    }

    public static void removePlayer(UUID playerUUID, UUID player) {
        LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).removePlayersUUID(player);
    }
}