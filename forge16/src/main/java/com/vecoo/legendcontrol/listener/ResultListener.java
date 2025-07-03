package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.util.WebhookUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
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
        PixelmonEntity pixelmonEntity = event.wpp.getFaintedPokemon().entity;

        if (pixelmonEntity.isLegendary() && LegendarySpawnListener.LEGENDS.remove(pixelmonEntity) && LegendControl.getInstance().getConfig().isNotifyLegendaryDefeat()) {
            ServerPlayerEntity player = event.player;

            UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyDefeat()
                    .replace("%player%", player.getName().getString())
                    .replace("%pokemon%", pixelmonEntity.getPokemonName()));

            WebhookUtils.defeatWebhook(pixelmonEntity, player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onStartCapture(CaptureEvent.StartCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon();

        if (pixelmonEntity.isLegendary() && LegendarySpawnListener.LEGENDS.remove(pixelmonEntity)) {
            SUB_LEGENDS.add(pixelmonEntity.getUUID());
        }
    }

    @SubscribeEvent
    public void onFailureCapture(CaptureEvent.FailedCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon();

        if (pixelmonEntity.isLegendary() && SUB_LEGENDS.remove(pixelmonEntity.getUUID())) {
            LegendarySpawnListener.LEGENDS.add(pixelmonEntity);
        }
    }


    @SubscribeEvent
    public void onCapture(CaptureEvent.SuccessfulCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon();

        if (pixelmonEntity.isLegendary() && SUB_LEGENDS.remove(pixelmonEntity.getUUID()) && LegendControl.getInstance().getConfig().isNotifyLegendaryCatch()) {
            ServerPlayerEntity player = event.getPlayer();

            UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyCatch()
                    .replace("%player%", player.getName().getString())
                    .replace("%pokemon%", pixelmonEntity.getPokemonName()));

            WebhookUtils.captureWebhook(pixelmonEntity, player);
        }
    }

    @SubscribeEvent
    public void onDespawn(EntityLeaveWorldEvent event) {
        if (event.getWorld().isClientSide() || !(event.getEntity() instanceof PixelmonEntity)) {
            return;
        }

        PixelmonEntity pixelmonEntity = (PixelmonEntity) event.getEntity();

        if (pixelmonEntity.isLegendary() && LegendarySpawnListener.LEGENDS.remove(pixelmonEntity) && LegendControl.getInstance().getConfig().isNotifyLegendaryDespawn()) {
            MinecraftForge.EVENT_BUS.post(new LegendControlEvent.ChunkDespawn(pixelmonEntity));

            UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyDespawn()
                    .replace("%pokemon%", pixelmonEntity.getPokemonName()));

            WebhookUtils.despawnWebhook(pixelmonEntity);
        }
    }
}
