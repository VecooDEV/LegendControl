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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashSet;
import java.util.Set;

public class LegendarySpawnListener {
    public static final Set<PixelmonEntity> legends = new HashSet<>();

    public static Set<PixelmonEntity> getLegends() {
        return legends;
    }

    @SubscribeEvent
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        PixelmonEntity pixelmonEntity = event.action.getOrCreateEntity();
        ServerPlayer player = (ServerPlayer) event.action.spawnLocation.cause;
        ServerConfig config = LegendControl.getInstance().getConfig();

        if (!config.isLegendaryRepeat() && LegendControlFactory.ServerProvider.getLastLegend().equals(pixelmonEntity.getPokemonName())) {
            LegendControlFactory.ServerProvider.addLegendaryChance(LegendSourceName.PIXELMON, LegendControl.getInstance().getConfig().getStepSpawnChance());
            event.setCanceled(true);
            return;
        }

        TaskTimer.builder()
                .delay(1L)
                .consume(task -> {
                    if (!pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                        task.cancel();
                        return;
                    }

                    if (config.isNotifyPersonalLegendarySpawn() && !player.hasDisconnected()) {
                        player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getSpawnPlayerLegendary()
                                .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                                .replace("%x%", String.valueOf((int) pixelmonEntity.getX()))
                                .replace("%y%", String.valueOf((int) pixelmonEntity.getY()))
                                .replace("%z%", String.valueOf((int) pixelmonEntity.getZ()))));
                    }

                    LegendControlFactory.ServerProvider.setLegendaryChance(LegendSourceName.PIXELMON, config.getBaseChance());
                    LegendControlFactory.ServerProvider.setLastLegend(pixelmonEntity.getPokemonName());
                    legends.add(pixelmonEntity);
                    setTimers(pixelmonEntity);
                    WebhookUtils.spawnWebhook(pixelmonEntity, event.action.spawnLocation.biome);
                }).build();
    }

    private void setTimers(PixelmonEntity pixelmonEntity) {
        ServerConfig config = LegendControl.getInstance().getConfig();

        if (config.getLocationTime() > 0) {
            TaskTimer.builder()
                    .delay(config.getLocationTime() * 20L)
                    .consume(task -> {
                        if (!legends.contains(pixelmonEntity) || !pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                            task.cancel();
                            return;
                        }

                        LegendControlEvent.Location event = new LegendControlEvent.Location(pixelmonEntity, pixelmonEntity.getX(), pixelmonEntity.getY(), pixelmonEntity.getZ());

                        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                            task.cancel();
                            return;
                        }

                        UtilChat.broadcast(LegendControl.getInstance().getLocale().getLocation()
                                .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                                .replace("%x%", String.valueOf((int) event.getX()))
                                .replace("%y%", String.valueOf((int) event.getY()))
                                .replace("%z%", String.valueOf((int) event.getZ())));

                        WebhookUtils.locationWebhook(pixelmonEntity);
                    }).build();
        }

        if (config.getDespawnTime() > 0) {
            TaskTimer.builder()
                    .delay(config.getDespawnTime() * 20L)
                    .consume(task -> {
                        if (!legends.contains(pixelmonEntity) || !pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                            task.cancel();
                            return;
                        }

                        if (NeoForge.EVENT_BUS.post(new LegendControlEvent.ForceDespawn(pixelmonEntity)).isCanceled()) {
                            task.cancel();
                            return;
                        }

                        if (pixelmonEntity.battleController != null) {
                            pixelmonEntity.battleController.endBattle();
                        }

                        pixelmonEntity.remove(Entity.RemovalReason.KILLED);
                    }).build();
        }
    }
}
