package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.util.WebhookUtils;
import lombok.val;
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
    public void onAttackDamage(AttackEvent.Damage event) { //We use this event because in 1.21.1 the BeatWildPixelmonEvent occurs later than EntityLeaveLevelEvent.
        if (event.willBeFatal()) {
            val pixelmonEntity = event.target.getEntity();

            if (pixelmonEntity != null && LegendarySpawnListener.LEGENDS.remove(pixelmonEntity)
                && LegendControl.getInstance().getServerConfig().isNotifyLegendaryDefeat()) {
                val player = event.user.getOwnerPlayer();
                String playerName;

                if (player == null) {
                    playerName = "Unknown";
                } else {
                    playerName = player.getName().getString();
                }

                UtilChat.broadcast(LegendControl.getInstance().getLocaleConfig().getNotifyDefeat()
                        .replace("%player%", playerName)
                        .replace("%pokemon%", pixelmonEntity.getPokemonName()));

                WebhookUtils.defeatWebhook(pixelmonEntity.getPokemon(), playerName);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onStartCapture(CaptureEvent.StartCapture event) {
        val pixelmonEntity = event.getPokemon().getEntity();

        if (LegendarySpawnListener.LEGENDS.remove(pixelmonEntity)) {
            SUB_LEGENDS.add(event.getPokemon().getUUID());
        }
    }

    @SubscribeEvent
    public void onSuccessfulCapture(CaptureEvent.SuccessfulCapture event) {
        val pokemon = event.getPokemon();

        if (SUB_LEGENDS.remove(pokemon.getUUID()) && LegendControl.getInstance().getServerConfig().isNotifyLegendaryCatch()) {
            val playerName = event.getPlayer().getName().getString();

            UtilChat.broadcast(LegendControl.getInstance().getLocaleConfig().getNotifyCatch()
                    .replace("%player%", playerName)
                    .replace("%pokemon%", pokemon.getTranslatedName().getString()));

            WebhookUtils.captureWebhook(pokemon, playerName);
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof PixelmonEntity pixelmonEntity) {
            if (SUB_LEGENDS.contains(pixelmonEntity.getUUID())) {
                LegendarySpawnListener.LEGENDS.add(pixelmonEntity);
            }
        }
    }

    @SubscribeEvent
    public void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof PixelmonEntity pixelmonEntity) {
            if (LegendarySpawnListener.LEGENDS.remove(pixelmonEntity)
                && LegendControl.getInstance().getServerConfig().isNotifyLegendaryDespawn()) {
                NeoForge.EVENT_BUS.post(new LegendControlEvent.ChunkDespawn(pixelmonEntity));

                UtilChat.broadcast(LegendControl.getInstance().getLocaleConfig().getNotifyDespawn()
                        .replace("%pokemon%", pixelmonEntity.getPokemonName()));

                WebhookUtils.despawnWebhook(pixelmonEntity.getPokemon());
            }
        }
    }
}
