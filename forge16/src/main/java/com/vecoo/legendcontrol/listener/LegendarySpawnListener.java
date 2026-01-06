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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LegendarySpawnListener {
    public static final List<PixelmonEntity> LEGENDS = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        val serverConfig = LegendControl.getInstance().getServerConfig();
        val player = (ServerPlayerEntity) event.action.spawnLocation.cause;
        val pixelmonEntity = event.action.getOrCreateEntity();

        if (!serverConfig.isLegendaryRepeat() && LegendControlService.getLastLegend().equals(pixelmonEntity.getPokemonName())) {
            LegendControlService.addChanceLegend(LegendSourceName.PIXELMON, serverConfig.getStepSpawnChance());
            event.setCanceled(true);
            return;
        }

        if (serverConfig.isNotifyPersonalLegendarySpawn()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocaleConfig().getSpawnPlayerLegendary()
                    .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                    .replace("%x%", String.valueOf(pixelmonEntity.getX()))
                    .replace("%y%", String.valueOf(pixelmonEntity.getY()))
                    .replace("%z%", String.valueOf(pixelmonEntity.getZ()))), Util.NIL_UUID);
        }

        LegendControlService.setChanceLegend(LegendSourceName.PIXELMON, serverConfig.getBaseChance());
        LegendControlService.setLastLegend(pixelmonEntity.getPokemonName());
        LEGENDS.add(pixelmonEntity);
        setTimers(pixelmonEntity);
        WebhookUtils.spawnWebhook(pixelmonEntity.getPokemon(), event.action.spawnLocation.biome);
    }

    private void setTimers(@Nonnull PixelmonEntity pixelmonEntity) {
        val serverConfig = LegendControl.getInstance().getServerConfig();

        if (serverConfig.getLocationTime() > 0) {
            TaskTimer.builder()
                    .delay(serverConfig.getLocationTime() * 20L)
                    .consume(task -> {
                        if (LEGENDS.contains(pixelmonEntity)) {
                            val event = new LegendControlEvent.Location(pixelmonEntity, pixelmonEntity.getX(), pixelmonEntity.getY(), pixelmonEntity.getZ());

                            if (!MinecraftForge.EVENT_BUS.post(event)) {
                                UtilChat.broadcast(LegendControl.getInstance().getLocaleConfig().getLocation()
                                        .replace("%pokemon%", pixelmonEntity.getPokemonName())
                                        .replace("%x%", String.valueOf(pixelmonEntity.getX()))
                                        .replace("%y%", String.valueOf(pixelmonEntity.getY()))
                                        .replace("%z%", String.valueOf(pixelmonEntity.getZ())));

                                WebhookUtils.locationWebhook(pixelmonEntity);
                            }
                        }
                    }).build();
        }

        if (serverConfig.getDespawnTime() > 0) {
            TaskTimer.builder()
                    .delay(serverConfig.getDespawnTime() * 20L)
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
