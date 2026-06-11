package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendaryCheckSpawnsEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.scheduler.TaskTimer;
import com.vecoo.extralib.util.ChatUtil;
import com.vecoo.extralib.util.TextUtil;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.api.service.LegendControlService;
import com.vecoo.legendcontrol.util.WebhookUtils;
import lombok.val;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LegendControlListener {
    public static final List<PixelmonEntity> LEGENDS = new ArrayList<>();

    private int currentTick = 0;

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
            player.sendMessage(TextUtil.formatMessage(LegendControl.getInstance().getLocaleConfig().getSpawnPlayerLegendary()
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
                    .execute(() -> {
                        if (LEGENDS.contains(pixelmonEntity)) {
                            val event = new LegendControlEvent.Location(pixelmonEntity, pixelmonEntity.getX(), pixelmonEntity.getY(), pixelmonEntity.getZ());

                            if (!MinecraftForge.EVENT_BUS.post(event)) {
                                ChatUtil.broadcast(LegendControl.getInstance().getLocaleConfig().getLocation()
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
                    .execute(() -> {
                        if (LEGENDS.contains(pixelmonEntity) && !MinecraftForge.EVENT_BUS.post(new LegendControlEvent.ForceDespawn(pixelmonEntity))) {
                            if (pixelmonEntity.battleController != null) {
                                pixelmonEntity.battleController.endBattle();
                            }

                            pixelmonEntity.remove();
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

        val particle = (BasicParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(serverConfig.getParticleName()));

        if (particle == null) {
            return;
        }

        for (PixelmonEntity entity : LegendControlListener.LEGENDS) {
            if (entity.level instanceof ServerWorld) {
                val world = (ServerWorld) entity.level;

                world.sendParticles(particle, entity.getX(), entity.getYCentre(), entity.getZ(), 3,
                        world.random.nextDouble() - 0.5, world.random.nextDouble() - 0.5, world.random.nextDouble() - 0.5, 0.1
                );
            }
        }
    }
}
