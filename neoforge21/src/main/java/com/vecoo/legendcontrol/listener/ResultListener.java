package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.util.WebhookUtils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ResultListener {
    public static Set<UUID> SUB_LEGENDS = new HashSet<>();

    @SubscribeEvent
    public void onBeatWild(BeatWildPixelmonEvent event) {
        PixelmonEntity pixelmonEntity = event.wpp.getFaintedPokemon().getEntity();

        if (pixelmonEntity.isLegendary() && LegendarySpawnListener.LEGENDS.remove(pixelmonEntity) && LegendControl.getInstance().getConfig().isNotifyLegendaryDefeat()) {
            ServerPlayer player = event.player;

            UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyDefeat()
                    .replace("%player%", player.getName().getString())
                    .replace("%pokemon%", pixelmonEntity.getPokemonName()));

            WebhookUtils.defeatWebhook(pixelmonEntity.getPokemon(), player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onStartCapture(CaptureEvent.StartCapture event) {
        PixelmonEntity pixelmonEntity = event.getPokemon().getEntity();

        if (pixelmonEntity.isLegendary() && pixelmonEntity.getUUID().equals(event.getPokemon().getUUID())) {
            LegendarySpawnListener.LEGENDS.remove(pixelmonEntity);
            SUB_LEGENDS.add(event.getPokemon().getUUID());
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.SuccessfulCapture event) {
        Pokemon pokemon = event.getPokemon();

        if (pokemon.isLegendary() && SUB_LEGENDS.remove(pokemon.getUUID()) && LegendControl.getInstance().getConfig().isNotifyLegendaryCatch()) {
            ServerPlayer player = event.getPlayer();

            UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyCatch()
                    .replace("%player%", player.getName().getString())
                    .replace("%pokemon%", pokemon.getTranslatedName().getString()));

            WebhookUtils.captureWebhook(pokemon, player);
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof PixelmonEntity pixelmonEntity)) {
            return;
        }

        if (SUB_LEGENDS.contains(pixelmonEntity.getUUID())) {
            LegendarySpawnListener.LEGENDS.add(pixelmonEntity);
        }
    }

    @SubscribeEvent
    public void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof PixelmonEntity pixelmonEntity)) {
            return;
        }

        if (pixelmonEntity.isLegendary() && LegendarySpawnListener.LEGENDS.remove(pixelmonEntity) && LegendControl.getInstance().getConfig().isNotifyLegendaryDespawn()) {
            NeoForge.EVENT_BUS.post(new LegendControlEvent.ChunkDespawn(pixelmonEntity));

            UtilChat.broadcast(LegendControl.getInstance().getLocale().getNotifyDespawn()
                    .replace("%pokemon%", pixelmonEntity.getPokemonName()));

            WebhookUtils.despawnWebhook(pixelmonEntity.getPokemon());
        }
    }
}
