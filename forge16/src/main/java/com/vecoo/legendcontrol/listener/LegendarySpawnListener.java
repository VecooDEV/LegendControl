package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.util.TaskUtils;
import com.vecoo.legendcontrol.util.WebhookUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
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

        TaskUtils.builder()
                .delay(1L)
                .consume(task -> {
                    if (!pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                        task.cancel();
                        return;
                    }

                    if (config.isNotifyPersonalLegendarySpawn() && !player.hasDisconnected()) {
                        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getSpawnPlayerLegendary()), Util.NIL_UUID);
                    }

                    LegendControlFactory.ServerProvider.setLegendaryChance(LegendSourceName.PIXELMON, config.getBaseChance());
                    legends.add(pixelmonEntity);
                    setTimers(pixelmonEntity);
                    WebhookUtils.spawnWebhook(pixelmonEntity);
                }).build();
    }

    private void setTimers(PixelmonEntity pixelmonEntity) {
        MinecraftServer server = LegendControl.getInstance().getServer();
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

                        double x = pixelmonEntity.getX();
                        double y = pixelmonEntity.getY();
                        double z = pixelmonEntity.getZ();

                        UtilChat.broadcast(LegendControl.getInstance().getLocale().getLocation()
                                .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                                .replace("%x%", String.valueOf((int) x))
                                .replace("%y%", String.valueOf((int) y))
                                .replace("%z%", String.valueOf((int) z)), server);

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

                        if (pixelmonEntity.battleController != null) {
                            pixelmonEntity.battleController.endBattle();
                        }

                        pixelmonEntity.remove();
                    }).build();
        }
    }
}
