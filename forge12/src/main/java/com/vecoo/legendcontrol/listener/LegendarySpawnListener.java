package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.api.service.LegendControlService;
import com.vecoo.legendcontrol.util.WebhookUtils;
import lombok.val;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class LegendarySpawnListener {
    public static final List<EntityPixelmon> LEGENDS = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        val serverConfig = LegendControl.getInstance().getServerConfig();
        val player = (EntityPlayerMP) event.action.spawnLocation.cause;
        val entityPixelmon = event.action.getOrCreateEntity();

        if (!serverConfig.isLegendaryRepeat() && LegendControlService.getLastLegend().equals(entityPixelmon.getPokemonName())) {
            LegendControlService.addChanceLegend(LegendSourceName.PIXELMON, serverConfig.getStepSpawnChance());
            event.setCanceled(true);
            return;
        }

        if (serverConfig.isNotifyPersonalLegendarySpawn()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocaleConfig().getSpawnPlayerLegendary()
                    .replace("%pokemon%", entityPixelmon.getSpecies().getPokemonName())
                    .replace("%x%", String.valueOf((int) entityPixelmon.posX))
                    .replace("%y%", String.valueOf((int) entityPixelmon.posY))
                    .replace("%z%", String.valueOf((int) entityPixelmon.posZ))));


            LegendControlService.setChanceLegend(LegendSourceName.PIXELMON, serverConfig.getBaseChance());
            LegendControlService.setLastLegend(entityPixelmon.getPokemonName());
            LEGENDS.add(entityPixelmon);
            setTimers(entityPixelmon);
            WebhookUtils.spawnWebhook(entityPixelmon.getPokemonData(), event.action.spawnLocation.biome);
        }
    }

    private void setTimers(EntityPixelmon entityPixelmon) {
        val serverConfig = LegendControl.getInstance().getServerConfig();

        if (serverConfig.getLocationTime() > 0) {
            TaskTimer.builder()
                    .delay(serverConfig.getLocationTime() * 20L)
                    .consume(task -> {
                        if (LEGENDS.contains(entityPixelmon)) {
                            val event = new LegendControlEvent.Location(
                                    entityPixelmon, entityPixelmon.posX, entityPixelmon.posY, entityPixelmon.posZ
                            );

                            if (!MinecraftForge.EVENT_BUS.post(event)) {
                                UtilChat.broadcast(LegendControl.getInstance().getLocaleConfig().getLocation()
                                        .replace("%pokemon%", entityPixelmon.getSpecies().getPokemonName())
                                        .replace("%x%", String.valueOf((int) event.getX()))
                                        .replace("%y%", String.valueOf((int) event.getY()))
                                        .replace("%z%", String.valueOf((int) event.getZ())));

                                WebhookUtils.locationWebhook(entityPixelmon);
                            }
                        }
                    }).build();
        }

        if (serverConfig.getDespawnTime() > 0) {
            TaskTimer.builder()
                    .delay(serverConfig.getDespawnTime() * 20L)
                    .consume(task -> {
                        if (LEGENDS.contains(entityPixelmon) && !MinecraftForge.EVENT_BUS.post(new LegendControlEvent.ForceDespawn(entityPixelmon))) {
                            if (entityPixelmon.battleController != null) {
                                entityPixelmon.battleController.endBattle();
                            }

                            entityPixelmon.setDead();
                        }
                    }).build();
        }
    }
}
