package com.vecoo.legendcontrol_defender.api.factory;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.PlayerTrustEvent;
import com.vecoo.legendcontrol_defender.storage.player.PlayerStorage;
import net.minecraftforge.common.MinecraftForge;

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

        public static void addPlayerTrust(UUID playerUUID, UUID targetUUID, boolean update) {
            if (!MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Add(playerUUID, targetUUID))) {
                LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).addPlayerTrust(targetUUID, update);
            }
        }

        public static void removePlayerTrust(UUID playerUUID, UUID targetUUID, boolean update) {
            if (!MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Remove(playerUUID, targetUUID))) {
                LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).removePlayerTrust(targetUUID, update);
            }
        }

        public static void removePlayersTrust(UUID playerUUID, boolean update) {
            if (!MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Clear(playerUUID))) {
                LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).clearPlayersTrust(update);
            }
        }
    }
}