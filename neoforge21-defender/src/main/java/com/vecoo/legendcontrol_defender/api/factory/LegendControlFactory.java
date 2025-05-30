package com.vecoo.legendcontrol_defender.api.factory;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.PlayerTrustEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Set;
import java.util.UUID;

public class LegendControlFactory {
    public static class PlayerProvider {
        public static Set<UUID> getPlayersTrust(UUID playerUUID) {
            return LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getPlayersTrust();
        }

        public static boolean hasPlayerTrust(UUID playerUUID, UUID targetUUID) {
            return LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getPlayersTrust().contains(targetUUID);
        }

        public static void addPlayerTrust(UUID playerUUID, UUID targetUUID) {
            if (!NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Add(playerUUID, targetUUID)).isCanceled()) {
                LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).addPlayerTrust(targetUUID);
            }
        }

        public static void removePlayerTrust(UUID playerUUID, UUID targetUUID) {
            if (!NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Remove(playerUUID, targetUUID)).isCanceled()) {
                LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).removePlayerTrust(targetUUID);
            }
        }

        public static void removePlayersTrust(UUID playerUUID) {
            if (!NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Clear(playerUUID)).isCanceled()) {
                LegendControlDefender.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).clearPlayersTrust();
            }
        }
    }
}