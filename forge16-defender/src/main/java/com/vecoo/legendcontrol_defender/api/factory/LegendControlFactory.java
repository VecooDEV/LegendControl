package com.vecoo.legendcontrol_defender.api.factory;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.storage.player.PlayerStorage;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LegendControlFactory {
    public static class PlayerProvider {
        public static Map<UUID, PlayerStorage> getMap() {
            return LegendControlDefender.getInstance().getPlayerProvider().getMap();
        }

        public static Set<UUID> getPlayersTrust(UUID playerUUID) {
            return LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getPlayersTrust();
        }

        public static boolean hasPlayerTrust(UUID playerUUID, UUID targetUUID) {
            return LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getPlayersTrust().contains(targetUUID);
        }

        public static void addPlayerTrust(UUID playerUUID, UUID targetUUID) {
            LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).addPlayerTrust(targetUUID, true);
        }

        public static void removePlayerTrust(UUID playerUUID, UUID targetUUID) {
            LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).removePlayerTrust(targetUUID, true);
        }

        public static void removePlayersTrust(UUID playerUUID) {
            LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).clearPlayersTrust(true);
        }
    }
}