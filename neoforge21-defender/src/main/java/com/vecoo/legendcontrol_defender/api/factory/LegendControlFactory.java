package com.vecoo.legendcontrol_defender.api.factory;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.storage.PlayerStorage;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LegendControlFactory {
    public static class PlayerProvider {
        public static Map<UUID, PlayerStorage> getMap() {
            return LegendControlDefender.getInstance().getPlayerProvider().getMap();
        }

        public static Set<UUID> getPlayersTrust(UUID playerUUID) {
            return LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).getPlayersTrust();
        }

        public static boolean hasPlayerTrust(UUID playerUUID, UUID targetUUID) {
            return LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).getPlayersTrust().contains(targetUUID);
        }

        public static void addPlayerTrust(UUID playerUUID, UUID targetUUID) {
            LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).addPlayerTrust(targetUUID);
        }

        public static void removePlayerTrust(UUID playerUUID, UUID targetUUID) {
            LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).removePlayerTrust(targetUUID);
        }

        public static void removePlayersTrust(UUID playerUUID) {
            LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).clearPlayersTrust();
        }
    }
}