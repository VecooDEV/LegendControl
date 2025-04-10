package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.util.WebhookUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ResultListener {
    public static Set<UUID> cachedLegends = new HashSet<>();

    @SubscribeEvent
    public void onBeatWild(BeatWildPixelmonEvent event) {
        PixelmonEntity pixelmonEntity = event.wpp.getFaintedPokemon().entity;

        if (!pixelmonEntity.isLegendary() || !LegendarySpawnListener.legends.remove(pixelmonEntity) || !LegendControl.getInstance().getConfig().isNotifyLegendaryDefeat()) {
            return;
        }

        ServerPlayerEntity player = event.player;

        UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyDefeat()
                .replace("%player%", player.getName().getString())
                .replace("%pokemon%", pixelmonEntity.getPokemonName()), LegendControl.getInstance().getServer());

        WebhookUtils.defeatWebhook(pixelmonEntity, player);
    }

    @SubscribeEvent
    public void onStartCapture(CaptureEvent.StartCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon();

        if (!pixelmonEntity.isLegendary() || !LegendarySpawnListener.legends.remove(pixelmonEntity)) {
            return;
        }

        cachedLegends.add(pixelmonEntity.getUUID());
    }

    @SubscribeEvent
    public void onFailureCapture(CaptureEvent.FailedCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon();

        if (!pixelmonEntity.isLegendary() || !cachedLegends.remove(pixelmonEntity.getUUID())) {
            return;
        }

        LegendarySpawnListener.legends.add(pixelmonEntity);
    }


    @SubscribeEvent
    public void onCapture(CaptureEvent.SuccessfulCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon();

        if (!pixelmonEntity.isLegendary() || !cachedLegends.remove(pixelmonEntity.getUUID()) || !LegendControl.getInstance().getConfig().isNotifyLegendaryCatch()) {
            return;
        }

        ServerPlayerEntity player = event.getPlayer();

        UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyCatch()
                .replace("%player%", player.getName().getString())
                .replace("%pokemon%", pixelmonEntity.getPokemonName()), LegendControl.getInstance().getServer());

        WebhookUtils.captureWebhook(pixelmonEntity, player);
    }

    @SubscribeEvent
    public void onDespawn(EntityLeaveWorldEvent event) {
        if (event.getWorld().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof PixelmonEntity)) {
            return;
        }

        PixelmonEntity pixelmonEntity = (PixelmonEntity) event.getEntity();

        if (!pixelmonEntity.isLegendary() || !LegendarySpawnListener.legends.remove(pixelmonEntity) || !LegendControl.getInstance().getConfig().isNotifyLegendaryDespawn()) {
            return;
        }

        UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyDespawn()
                .replace("%pokemon%", pixelmonEntity.getPokemonName()), LegendControl.getInstance().getServer());

        WebhookUtils.despawnWebhook(pixelmonEntity);
    }
}
