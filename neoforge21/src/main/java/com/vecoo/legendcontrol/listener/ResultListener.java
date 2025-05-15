package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.util.WebhookUtils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ResultListener {
    public static Set<UUID> cachedLegends = new HashSet<>();

    @SubscribeEvent
    public void onBeatWild(BeatWildPixelmonEvent event) {
        PixelmonEntity pixelmonEntity = event.wpp.getFaintedPokemon().getEntity();

        if (!pixelmonEntity.isLegendary() || !LegendarySpawnListener.legends.remove(pixelmonEntity) || !LegendControl.getInstance().getConfig().isNotifyLegendaryDefeat()) {
            return;
        }

        ServerPlayer player = event.player;

        UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyDefeat()
                .replace("%player%", player.getName().getString())
                .replace("%pokemon%", pixelmonEntity.getPokemonName()), LegendControl.getInstance().getServer());

        WebhookUtils.defeatWebhook(pixelmonEntity, player);
    }

    @SubscribeEvent
    public void onStartCapture(CaptureEvent.StartCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon().getPixelmonEntity().orElse(null);

        if (pixelmonEntity == null || !pixelmonEntity.isLegendary() || !LegendarySpawnListener.legends.remove(pixelmonEntity)) {
            return;
        }

        cachedLegends.add(pixelmonEntity.getUUID());
    }

    @SubscribeEvent
    public void onFailureCapture(CaptureEvent.FailedCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon().getPixelmonEntity().orElse(null);

        if (pixelmonEntity == null || !pixelmonEntity.isLegendary() || !cachedLegends.remove(pixelmonEntity.getUUID())) {
            return;
        }

        LegendarySpawnListener.legends.add(pixelmonEntity);
    }


    @SubscribeEvent
    public void onCapture(CaptureEvent.SuccessfulCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon().getPixelmonEntity().orElse(null);

        if (pixelmonEntity == null || !pixelmonEntity.isLegendary() || !cachedLegends.remove(pixelmonEntity.getUUID()) || !LegendControl.getInstance().getConfig().isNotifyLegendaryCatch()) {
            return;
        }

        ServerPlayer player = event.getPlayer();

        UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyCatch()
                .replace("%player%", player.getName().getString())
                .replace("%pokemon%", pixelmonEntity.getPokemonName()), LegendControl.getInstance().getServer());

        WebhookUtils.captureWebhook(pixelmonEntity, player);
    }

    @SubscribeEvent
    public void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof PixelmonEntity pixelmonEntity)) {
            return;
        }

        if (!pixelmonEntity.isLegendary() || !LegendarySpawnListener.legends.remove(pixelmonEntity) || !LegendControl.getInstance().getConfig().isNotifyLegendaryDespawn()) {
            return;
        }

        NeoForge.EVENT_BUS.post(new LegendControlEvent.ChunkDespawn(pixelmonEntity));

        UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyDespawn()
                .replace("%pokemon%", pixelmonEntity.getPokemonName()), LegendControl.getInstance().getServer());

        WebhookUtils.despawnWebhook(pixelmonEntity);
    }
}
