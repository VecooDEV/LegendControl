package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.util.TaskUtils;
import com.vecoo.legendcontrol.util.WebhookUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LegendarySpawnListener {
    public static final Set<PixelmonEntity> legends = ConcurrentHashMap.newKeySet();

    public static Set<PixelmonEntity> getLegends() {
        return legends;
    }

    @SubscribeEvent
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        PixelmonEntity pixelmonEntity = event.action.getOrCreateEntity();
        ServerPlayerEntity player = (ServerPlayerEntity) event.action.spawnLocation.cause;
        ServerConfig config = LegendControl.getInstance().getConfig();

        if (!config.isLegendaryRepeat() && LegendControlFactory.ServerProvider.getLastLegend().equals(pixelmonEntity.getPokemonName())) {
            LegendControlFactory.ServerProvider.addLegendaryChance(LegendSourceName.PIXELMON, LegendControl.getInstance().getConfig().getStepSpawnChance());
            event.setCanceled(true);
            return;
        }

        TaskUtils.builder()
                .delay(1L)
                .consume(task -> {
                    if (!pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                        task.cancel();
                        return;
                    }

                    if (config.isNotifyPersonalLegendarySpawn() && !player.hasDisconnected()) {
                        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getSpawnPlayerLegendary()
                                .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                                .replace("%x%", String.valueOf((int) pixelmonEntity.getX()))
                                .replace("%y%", String.valueOf((int) pixelmonEntity.getY()))
                                .replace("%z%", String.valueOf((int) pixelmonEntity.getZ()))), Util.NIL_UUID);
                    }

                    LegendControlFactory.ServerProvider.setLegendaryChance(LegendSourceName.PIXELMON, config.getBaseChance());
                    LegendControlFactory.ServerProvider.setLastLegend(pixelmonEntity.getPokemonName());
                    legends.add(pixelmonEntity);
                    setTimers(pixelmonEntity);
                    WebhookUtils.spawnWebhook(pixelmonEntity);
                }).build();
    }

    private void setTimers(PixelmonEntity pixelmonEntity) {
        ServerConfig config = LegendControl.getInstance().getConfig();

        if (config.getLocationTime() > 0) {
            TaskUtils.builder()
                    .delay(config.getLocationTime() * 20L)
                    .consume(task -> {
                        if (!legends.contains(pixelmonEntity) || !pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                            task.cancel();
                            return;
                        }

                        if (pixelmonEntity.level == null) {
                            task.cancel();
                            return;
                        }

                        if (MinecraftForge.EVENT_BUS.post(new LegendControlEvent.Location(pixelmonEntity))) {
                            task.cancel();
                            return;
                        }

                        UtilChat.broadcast(LegendControl.getInstance().getLocale().getLocation()
                                .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                                .replace("%x%", String.valueOf((int) pixelmonEntity.getX()))
                                .replace("%y%", String.valueOf((int) pixelmonEntity.getY()))
                                .replace("%z%", String.valueOf((int) pixelmonEntity.getZ())), LegendControl.getInstance().getServer());

                        WebhookUtils.locationWebhook(pixelmonEntity);
                    }).build();
        }

        if (config.getDespawnTime() > 0) {
            TaskUtils.builder()
                    .delay(config.getDespawnTime() * 20L)
                    .consume(task -> {
                        if (!legends.contains(pixelmonEntity) || !pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                            task.cancel();
                            return;
                        }

                        if (MinecraftForge.EVENT_BUS.post(new LegendControlEvent.ForceDespawn(pixelmonEntity))) {
                            task.cancel();
                            return;
                        }

                        if (pixelmonEntity.battleController != null) {
                            pixelmonEntity.battleController.endBattle();
                        }

                        pixelmonEntity.remove();
                    }).build();
        }
    }
}
