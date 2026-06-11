package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendaryCheckSpawnsEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.extralib.scheduler.TaskTimer;
import com.vecoo.extralib.util.ChatUtil;
import com.vecoo.extralib.util.TextUtil;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.api.service.LegendControlService;
import com.vecoo.legendcontrol.util.WebhookUtils;
import lombok.val;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class LegendControlListener {
    public static final List<EntityPixelmon> LEGENDS = new ArrayList<>();

    private long currentTick = 0;

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
            player.sendMessage(TextUtil.formatMessage(LegendControl.getInstance().getLocaleConfig().getSpawnPlayerLegendary()
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
                    .execute(() -> {
                        if (LEGENDS.contains(entityPixelmon)) {
                            val event = new LegendControlEvent.Location(entityPixelmon, entityPixelmon.posX, entityPixelmon.posY, entityPixelmon.posZ);

                            if (!MinecraftForge.EVENT_BUS.post(event)) {
                                ChatUtil.broadcast(LegendControl.getInstance().getLocaleConfig().getLocation()
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
                    .execute(() -> {
                        if (LEGENDS.contains(entityPixelmon) && !MinecraftForge.EVENT_BUS.post(new LegendControlEvent.ForceDespawn(entityPixelmon))) {
                            if (entityPixelmon.battleController != null) {
                                entityPixelmon.battleController.endBattle();
                            }

                            entityPixelmon.setDead();
                        }
                    }).build();
        }
    }

    @SubscribeEvent
    public void onLegendaryCheckSpawns(LegendaryCheckSpawnsEvent event) {
        event.shouldShowTime = false;
        event.shouldShowChance = false;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        val serverConfig = LegendControl.getInstance().getServerConfig();

        if (event.phase == TickEvent.Phase.START || !serverConfig.isLegendaryParticle() || ++this.currentTick % 20 != 0) {
            return;
        }

        val particle = EnumParticleTypes.getByName(serverConfig.getParticleName());

        if (particle == null) {
            return;
        }

        LegendControlListener.LEGENDS.removeIf(entity -> entity == null || !entity.isEntityAlive() || entity.hasOwner());

        for (EntityPixelmon entity : LegendControlListener.LEGENDS) {
            if (entity.world instanceof WorldServer) {
                val world = (WorldServer) entity.world;

                world.spawnParticle(particle, entity.posX, entity.getYCentre(), entity.posZ, 3,
                        world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 0.5, 0.1);
            }
        }
    }
}