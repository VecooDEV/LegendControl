package com.vecoo.legendcontrol_defender.api.factory;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.PlayerTrustEvent;
import com.vecoo.legendcontrol_defender.storage.PlayerStorage;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LegendControlFactory {
    public static class PlayerProvider {
        @NotNull
        public static Map<UUID, PlayerStorage> getStorage() {
            return LegendControlDefender.getInstance().getPlayerProvider().getStorage();
        }

        @NotNull
        public static Set<UUID> getPlayersTrust(@NotNull UUID playerUUID) {
            return LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).getPlayersTrust();
        }

        public static boolean hasPlayerTrust(@NotNull UUID playerUUID, @NotNull UUID targetUUID) {
            return LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).getPlayersTrust().contains(targetUUID);
        }

        public static boolean addPlayerTrust(@NotNull UUID playerUUID, @NotNull UUID targetUUID) {
            if (NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Add(playerUUID, targetUUID)).isCanceled()) {
                return false;
            }

            LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).addPlayerTrust(targetUUID);
            return true;
        }

        public static boolean removePlayerTrust(@NotNull UUID playerUUID, @NotNull UUID targetUUID) {
            if (NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Remove(playerUUID, targetUUID)).isCanceled()) {
                return false;
            }

            LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).removePlayerTrust(targetUUID);
            return true;
        }

        public static boolean clearPlayersTrust(@NotNull UUID playerUUID) {
            if (NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Clear(playerUUID)).isCanceled()) {
                return false;
            }

            LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).clearPlayersTrust();
            return true;
        }
    }
}