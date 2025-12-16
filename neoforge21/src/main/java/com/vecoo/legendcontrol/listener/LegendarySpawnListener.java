package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.api.service.LegendControlService;
import com.vecoo.legendcontrol.util.WebhookUtils;
import lombok.val;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LegendarySpawnListener {
    public static final List<PixelmonEntity> LEGENDS = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        val serverConfig = LegendControl.getInstance().getServerConfig();
        val player = (ServerPlayer) event.action.spawnLocation.cause;
        val pixelmonEntity = event.action.getOrCreateEntity();

        if (!serverConfig.isLegendaryRepeat() && LegendControlService.getLastLegend().equals(pixelmonEntity.getPokemonName())) {
            LegendControlService.addChanceLegend(LegendSourceName.PIXELMON, serverConfig.getStepSpawnChance());
            event.setCanceled(true);
            return;
        }

        if (serverConfig.isNotifyPersonalLegendarySpawn()) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocaleConfig().getSpawnPlayerLegendary()
                    .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                    .replace("%x%", String.valueOf(pixelmonEntity.getBlockX()))
                    .replace("%y%", String.valueOf(pixelmonEntity.getBlockY()))
                    .replace("%z%", String.valueOf(pixelmonEntity.getBlockZ()))));
        }

        LegendControlService.setChanceLegend(LegendSourceName.PIXELMON, serverConfig.getBaseChance());
        LegendControlService.setLastLegend(pixelmonEntity.getPokemonName());
        LEGENDS.add(pixelmonEntity);
        setTimers(pixelmonEntity);
        WebhookUtils.spawnWebhook(pixelmonEntity.getPokemon(), event.action.spawnLocation.biome);
    }

    private void setTimers(@NotNull PixelmonEntity pixelmonEntity) {
        val serverConfig = LegendControl.getInstance().getServerConfig();

        if (serverConfig.getLocationTime() > 0) {
            TaskTimer.builder()
                    .delay(serverConfig.getLocationTime() * 20L)
                    .consume(task -> {
                        if (LEGENDS.contains(pixelmonEntity)) {
                            val event = new LegendControlEvent.Location(pixelmonEntity, pixelmonEntity.getX(), pixelmonEntity.getY(), pixelmonEntity.getZ());

                            if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
                                UtilChat.broadcast(LegendControl.getInstance().getLocaleConfig().getLocation()
                                        .replace("%pokemon%", pixelmonEntity.getPokemonName())
                                        .replace("%x%", String.valueOf(pixelmonEntity.getBlockX()))
                                        .replace("%y%", String.valueOf(pixelmonEntity.getBlockY()))
                                        .replace("%z%", String.valueOf(pixelmonEntity.getBlockZ())));

                                WebhookUtils.locationWebhook(pixelmonEntity);
                            }
                        }
                    }).build();
        }

        if (serverConfig.getDespawnTime() > 0) {
            TaskTimer.builder()
                    .delay(serverConfig.getDespawnTime() * 20L)
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
