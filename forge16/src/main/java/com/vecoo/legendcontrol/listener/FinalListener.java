package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.util.WebhookUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.UUID;

public class FinalListener {
    public static HashSet<UUID> cachedLegends = new HashSet<>();

    @SubscribeEvent
    public void onBeatWild(BeatWildPixelmonEvent event) {
        PixelmonEntity pixelmonEntity = event.wpp.getFaintedPokemon().entity;

        if (!pixelmonEntity.isLegendary() || !LegendControlFactory.ServerProvider.removeLegends(pixelmonEntity.getUUID()) || !LegendControl.getInstance().getConfig().isNotifyLegendaryDefeat()) {
            return;
        }

        ServerPlayerEntity player = event.player;

        String pokemonName = pixelmonEntity.getPokemonName();

        UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyDefeat()
                .replace("%player%", player.getName().getString())
                .replace("%pokemon%", pokemonName), LegendControl.getInstance().getServer());

        WebhookUtils.defeatWebhook(pixelmonEntity, player);
    }

    @SubscribeEvent
    public void onStartCapture(CaptureEvent.StartCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon();

        if (!pixelmonEntity.isLegendary() || !LegendarySpawnListener.legendaryList.contains(pixelmonEntity)) {
            return;
        }

        LegendControlFactory.ServerProvider.removeLegends(pixelmonEntity.getUUID());
        cachedLegends.add(pixelmonEntity.getUUID());
    }

    @SubscribeEvent
    public void onFailureCapture(CaptureEvent.FailedCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon();

        if (!pixelmonEntity.isLegendary() || cachedLegends.contains(pixelmonEntity.getUUID())) {
            return;
        }

        LegendarySpawnListener.legendaryList.add(pixelmonEntity);
        cachedLegends.remove(pixelmonEntity.getUUID());
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

        if (!pixelmonEntity.isLegendary() || !LegendControlFactory.ServerProvider.removeLegends(pixelmonEntity.getUUID()) || !LegendControl.getInstance().getConfig().isNotifyLegendaryDespawn()) {
            return;
        }

        UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyDespawn()
                .replace("%pokemon%", pixelmonEntity.getPokemonName()), LegendControl.getInstance().getServer());

        WebhookUtils.despawnWebhook(pixelmonEntity);
    }
}
