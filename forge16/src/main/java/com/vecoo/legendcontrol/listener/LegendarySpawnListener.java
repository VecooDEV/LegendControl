package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.util.WebhookUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class LegendarySpawnListener {
    public static final List<PixelmonEntity> LEGENDS = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        ServerConfig config = LegendControl.getInstance().getConfig();
        ServerPlayerEntity player = (ServerPlayerEntity) event.action.spawnLocation.cause;
        PixelmonEntity pixelmonEntity = event.action.getOrCreateEntity();

        if (!config.isLegendaryRepeat() && LegendControlFactory.ServerProvider.getLastLegend().equals(pixelmonEntity.getPokemonName())) {
            LegendControlFactory.ServerProvider.addChanceLegend(LegendSourceName.PIXELMON, config.getStepSpawnChance());
            event.setCanceled(true);
            return;
        }

        TaskTimer.builder()
                .delay(1L)
                .consume(task -> {
                    if (pixelmonEntity.isAlive()) {
                        if (config.isNotifyPersonalLegendarySpawn() && !player.hasDisconnected()) {
                            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getSpawnPlayerLegendary()
                                    .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                                    .replace("%x%", String.valueOf((int) pixelmonEntity.getX()))
                                    .replace("%y%", String.valueOf((int) pixelmonEntity.getY()))
                                    .replace("%z%", String.valueOf((int) pixelmonEntity.getZ()))), Util.NIL_UUID);
                        }

                        LegendControlFactory.ServerProvider.setChanceAndLastLegend(LegendSourceName.PIXELMON, LegendControl.getInstance().getConfig().getBaseChance(), pixelmonEntity.getPokemonName());
                        LEGENDS.add(pixelmonEntity);
                        setTimers(pixelmonEntity);
                        WebhookUtils.spawnWebhook(pixelmonEntity, event.action.spawnLocation.biome);
                    }
                }).build();
    }

    private void setTimers(PixelmonEntity pixelmonEntity) {
        ServerConfig config = LegendControl.getInstance().getConfig();

        if (config.getLocationTime() > 0) {
            TaskTimer.builder()
                    .delay(config.getLocationTime() * 20L)
                    .consume(task -> {
                        if (LEGENDS.contains(pixelmonEntity)) {
                            LegendControlEvent.Location event = new LegendControlEvent.Location(pixelmonEntity, pixelmonEntity.getX(), pixelmonEntity.getY(), pixelmonEntity.getZ());

                            if (!MinecraftForge.EVENT_BUS.post(event)) {
                                UtilChat.broadcast(LegendControl.getInstance().getLocale().getLocation()
                                        .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                                        .replace("%x%", String.valueOf((int) event.getX()))
                                        .replace("%y%", String.valueOf((int) event.getY()))
                                        .replace("%z%", String.valueOf((int) event.getZ())));

                                WebhookUtils.locationWebhook(pixelmonEntity);
                            }
                        }
                    }).build();
        }

        if (config.getDespawnTime() > 0) {
            TaskTimer.builder()
                    .delay(config.getDespawnTime() * 20L)
                    .consume(task -> {
                        if (LEGENDS.contains(pixelmonEntity) && !MinecraftForge.EVENT_BUS.post(new LegendControlEvent.ForceDespawn(pixelmonEntity))) {
                            if (pixelmonEntity.battleController != null) {
                                pixelmonEntity.battleController.endBattle();
                            }

                            pixelmonEntity.remove();
                        }
                    }).build();
        }
    }
}
