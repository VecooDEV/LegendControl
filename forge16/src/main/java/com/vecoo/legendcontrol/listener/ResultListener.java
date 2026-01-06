package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.util.WebhookUtils;
import lombok.val;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ResultListener {
    public static Set<UUID> SUB_LEGENDS = new HashSet<>();

    @SubscribeEvent
    public void onBeatWild(BeatWildPixelmonEvent event) {
        val pixelmonEntity = event.wpp.getFaintedPokemon().entity;

        if (LegendarySpawnListener.LEGENDS.remove(pixelmonEntity) && LegendControl.getInstance().getServerConfig().isNotifyLegendaryDefeat()) {
            val playerName = event.player.getName().getString();

            UtilChat.broadcast(LegendControl.getInstance().getLocaleConfig().getNotifyDefeat()
                    .replace("%player%", playerName)
                    .replace("%pokemon%", pixelmonEntity.getPokemonName()));

            WebhookUtils.defeatWebhook(pixelmonEntity.getPokemon(), playerName);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onStartCapture(CaptureEvent.StartCapture event) {
        val pixelmonEntity = event.getPokemon();

        if (LegendarySpawnListener.LEGENDS.remove(pixelmonEntity)) {
            SUB_LEGENDS.add(pixelmonEntity.getUUID());
        }
    }

    @SubscribeEvent
    public void onFailureCapture(CaptureEvent.FailedCapture event) {
        val pixelmonEntity = event.getPokemon();

        if (SUB_LEGENDS.remove(pixelmonEntity.getUUID())) {
            LegendarySpawnListener.LEGENDS.add(pixelmonEntity);
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.SuccessfulCapture event) {
        val pixelmonEntity = event.getPokemon();

        if (SUB_LEGENDS.remove(pixelmonEntity.getUUID()) && LegendControl.getInstance().getServerConfig().isNotifyLegendaryCatch()) {
            val playerName = event.getPlayer().getName().getString();

            UtilChat.broadcast(LegendControl.getInstance().getLocaleConfig().getNotifyCatch()
                    .replace("%player%", playerName)
                    .replace("%pokemon%", pixelmonEntity.getPokemonName()));

            WebhookUtils.captureWebhook(pixelmonEntity.getPokemon(), playerName);
        }
    }

    @SubscribeEvent
    public void onDespawn(EntityLeaveWorldEvent event) {
        if (!event.getWorld().isClientSide() && event.getEntity() instanceof PixelmonEntity) {
            val pixelmonEntity = (PixelmonEntity) event.getEntity();

            if (LegendarySpawnListener.LEGENDS.remove(pixelmonEntity) && LegendControl.getInstance().getServerConfig().isNotifyLegendaryDespawn()) {
                MinecraftForge.EVENT_BUS.post(new LegendControlEvent.ChunkDespawn(pixelmonEntity));

                UtilChat.broadcast(LegendControl.getInstance().getLocaleConfig().getNotifyDespawn()
                        .replace("%pokemon%", pixelmonEntity.getPokemonName()));

                WebhookUtils.despawnWebhook(pixelmonEntity.getPokemon());
            }
        }
    }
}
