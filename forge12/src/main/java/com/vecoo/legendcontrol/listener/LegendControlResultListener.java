package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.vecoo.extralib.util.ChatUtil;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.util.WebhookUtils;
import lombok.val;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LegendControlResultListener {
    public static Set<UUID> SUB_LEGENDS = new HashSet<>();

    @SubscribeEvent
    public void onBeatWild(BeatWildPixelmonEvent event) {
        val entityPixelmon = event.wpp.getFaintedPokemon().entity;

        if (LegendControlListener.LEGENDS.remove(entityPixelmon) && LegendControl.getInstance().getServerConfig().isNotifyLegendaryDefeat()) {
            ChatUtil.broadcast(LegendControl.getInstance().getLocaleConfig().getNotifyDefeat()
                    .replace("%player%", event.player.getName())
                    .replace("%pokemon%", entityPixelmon.getPokemonName()));

            WebhookUtils.defeatWebhook(entityPixelmon.getPokemonData(), event.player.getName());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onStartCapture(CaptureEvent.StartCapture event) {
        val entityPixelmon = event.getPokemon();

        if (LegendControlListener.LEGENDS.remove(entityPixelmon)) {
            SUB_LEGENDS.add(entityPixelmon.getUniqueID());
        }
    }

    @SubscribeEvent
    public void onFailureCapture(CaptureEvent.FailedCapture event) {
        val entityPixelmon = event.getPokemon();

        if (SUB_LEGENDS.remove(entityPixelmon.getUniqueID())) {
            LegendControlListener.LEGENDS.add(entityPixelmon);
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.SuccessfulCapture event) {
        val entityPixelmon = event.getPokemon();

        if (SUB_LEGENDS.remove(entityPixelmon.getUniqueID()) && LegendControl.getInstance().getServerConfig().isNotifyLegendaryCatch()) {
            ChatUtil.broadcast(LegendControl.getInstance().getLocaleConfig().getNotifyCatch()
                    .replace("%player%", event.player.getName())
                    .replace("%pokemon%", entityPixelmon.getPokemonName()));

            WebhookUtils.captureWebhook(entityPixelmon.getPokemonData(), event.player.getName());
        }
    }
}
