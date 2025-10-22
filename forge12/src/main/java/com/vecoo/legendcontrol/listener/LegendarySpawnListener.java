package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.util.WebhookUtils;
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
        ServerConfig config = LegendControl.getInstance().getConfig();
        EntityPlayerMP player = (EntityPlayerMP) event.action.spawnLocation.cause;
        EntityPixelmon entityPixelmon = event.action.getOrCreateEntity();

        if (!config.isLegendaryRepeat() && LegendControlFactory.ServerProvider.getLastLegend().equals(entityPixelmon.getPokemonName())) {
            LegendControlFactory.ServerProvider.addChanceLegend(LegendSourceName.PIXELMON, config.getStepSpawnChance());
            event.setCanceled(true);
            return;
        }

        if (config.isNotifyPersonalLegendarySpawn() && !player.hasDisconnected()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getSpawnPlayerLegendary()
                    .replace("%pokemon%", entityPixelmon.getSpecies().getPokemonName())
                    .replace("%x%", String.valueOf((int) entityPixelmon.posX))
                    .replace("%y%", String.valueOf((int) entityPixelmon.posY))
                    .replace("%z%", String.valueOf((int) entityPixelmon.posZ))));


            LegendControlFactory.ServerProvider.setChanceLegend(LegendSourceName.PIXELMON, config.getBaseChance());
            LegendControlFactory.ServerProvider.setLastLegend(entityPixelmon.getPokemonName());
            LEGENDS.add(entityPixelmon);
            setTimers(entityPixelmon);
            WebhookUtils.spawnWebhook(entityPixelmon.getPokemonData(), event.action.spawnLocation.biome);
        }
    }

    private void setTimers(EntityPixelmon entityPixelmon) {
        ServerConfig config = LegendControl.getInstance().getConfig();

        if (config.getLocationTime() > 0) {
            TaskTimer.builder()
                    .delay(config.getLocationTime() * 20L)
                    .consume(task -> {
                        if (LEGENDS.contains(entityPixelmon)) {
                            LegendControlEvent.Location event = new LegendControlEvent.Location(
                                    entityPixelmon, entityPixelmon.posX, entityPixelmon.posY, entityPixelmon.posZ
                            );

                            if (!MinecraftForge.EVENT_BUS.post(event)) {
                                UtilChat.broadcast(LegendControl.getInstance().getLocale().getLocation()
                                        .replace("%pokemon%", entityPixelmon.getSpecies().getPokemonName())
                                        .replace("%x%", String.valueOf((int) event.getX()))
                                        .replace("%y%", String.valueOf((int) event.getY()))
                                        .replace("%z%", String.valueOf((int) event.getZ())));

                                WebhookUtils.locationWebhook(entityPixelmon);
                            }
                        }
                    }).build();
        }

        if (config.getDespawnTime() > 0) {
            TaskTimer.builder()
                    .delay(config.getDespawnTime() * 20L)
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
