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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class LegendarySpawnListener {
    public static final List<PixelmonEntity> LEGENDS = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        ServerConfig config = LegendControl.getInstance().getConfig();
        ServerPlayer player = (ServerPlayer) event.action.spawnLocation.cause;
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
                            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getSpawnPlayerLegendary()
                                    .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                                    .replace("%x%", String.valueOf(pixelmonEntity.getBlockX()))
                                    .replace("%y%", String.valueOf(pixelmonEntity.getBlockY()))
                                    .replace("%z%", String.valueOf(pixelmonEntity.getBlockZ()))));
                        }

                        LegendControlFactory.ServerProvider.setChanceAndLastLegend(LegendSourceName.PIXELMON, LegendControl.getInstance().getConfig().getBaseChance(), pixelmonEntity.getPokemonName());
                        LEGENDS.add(pixelmonEntity);
                        setTimers(pixelmonEntity);
                        WebhookUtils.spawnWebhook(pixelmonEntity.getPokemon(), event.action.spawnLocation.biome);
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

                            if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
                                UtilChat.broadcast(LegendControl.getInstance().getLocale().getLocation()
                                        .replace("%pokemon%", pixelmonEntity.getPokemonName())
                                        .replace("%x%", String.valueOf(pixelmonEntity.getBlockX()))
                                        .replace("%y%", String.valueOf(pixelmonEntity.getBlockY()))
                                        .replace("%z%", String.valueOf(pixelmonEntity.getBlockZ())));

                                WebhookUtils.locationWebhook(pixelmonEntity);
                            }
                        }
                    }).build();
        }

        if (config.getDespawnTime() > 0) {
            TaskTimer.builder()
                    .delay(config.getDespawnTime() * 20L)
                    .consume(task -> {
                        if (LEGENDS.contains(pixelmonEntity) && !NeoForge.EVENT_BUS.post(new LegendControlEvent.ForceDespawn(pixelmonEntity)).isCanceled()) {
                            if (pixelmonEntity.battleController != null) {
                                pixelmonEntity.battleController.endBattle();
                            }

                            pixelmonEntity.remove(Entity.RemovalReason.KILLED);
                        }
                    }).build();
        }
    }
}
