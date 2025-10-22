package com.vecoo.legendcontrol_defender.api.factory;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.PlayerTrustEvent;
import com.vecoo.legendcontrol_defender.storage.PlayerStorage;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LegendControlFactory {
    public static class PlayerProvider {
        @Nonnull
        public static Map<UUID, PlayerStorage> getStorage() {
            return LegendControlDefender.getInstance().getPlayerProvider().getStorage();
        }

        @Nonnull
        public static Set<UUID> getPlayersTrust(@Nonnull UUID playerUUID) {
            return LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).getPlayersTrust();
        }

        public static boolean hasPlayerTrust(@Nonnull UUID playerUUID, @Nonnull UUID targetUUID) {
            return LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).getPlayersTrust().contains(targetUUID);
        }

        public static boolean addPlayerTrust(@Nonnull UUID playerUUID, @Nonnull UUID targetUUID) {
            if (MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Add(playerUUID, targetUUID))) {
                return false;
            }

            LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).addPlayerTrust(targetUUID);
            return true;
        }

        public static boolean removePlayerTrust(@Nonnull UUID playerUUID, @Nonnull UUID targetUUID) {
            if (MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Remove(playerUUID, targetUUID))) {
                return false;
            }

            LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).removePlayerTrust(targetUUID);
            return true;
        }

        public static boolean clearPlayersTrust(@Nonnull UUID playerUUID) {
            if (MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Clear(playerUUID))) {
                return false;
            }

            LegendControlDefender.getInstance().getPlayerProvider().getStorage(playerUUID).clearPlayersTrust();
            return true;
        }
    }
}