package com.vecoo.legendcontrol.listener;

import com.vecoo.legendcontrol.utils.data.UtilsTrust;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.IOException;
import java.util.UUID;

public class PlayerLoggedInListener {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            UUID uuid = event.player.getUniqueID();
            if (!UsernameCache.containsUUID(uuid) || !UtilsTrust.getDataMap().containsKey(uuid)) {
                UtilsTrust.writeToGSON(uuid, uuid, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
