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
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LegendControlListener {
    @NotNull
    public static final List<PixelmonEntity> LEGENDS = new ArrayList<>();

    private long currentTick = 0;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLegendarySpawnDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        val serverConfig = LegendControl.getInstance().getServerConfig();
        val player = (ServerPlayer) event.action.spawnLocation.cause;
        val pixelmonEntity = event.action.getOrCreateEntity();

        if (!serverConfig.isLegendaryRepeat() && LegendControlService.getLastLegend().equals(pixelmonEntity.getPokemonName())) {
            LegendControlService.addChanceLegend(LegendSourceName.PIXELMON, serverConfig.getStepSpawnChance());
            event.setCanceled(true);
            return;
        }

        if (serverConfig.isNotifyPersonalLegendarySpawn()) {
            player.sendSystemMessage(TextUtil.formatMessage(LegendControl.getInstance().getLocaleConfig().getSpawnPlayerLegendary()
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
                    .execute(() -> {
                        if (LEGENDS.contains(pixelmonEntity)) {
                            val event = new LegendControlEvent.Location(pixelmonEntity, pixelmonEntity.getX(), pixelmonEntity.getY(), pixelmonEntity.getZ());

                            if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
                                ChatUtil.broadcast(LegendControl.getInstance().getLocaleConfig().getLocation()
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
                    .execute(() -> {
                        if (LEGENDS.contains(pixelmonEntity) && !NeoForge.EVENT_BUS.post(new LegendControlEvent.ForceDespawn(pixelmonEntity)).isCanceled()) {
                            if (pixelmonEntity.battleController != null) {
                                pixelmonEntity.battleController.endBattle();
                            }

                            pixelmonEntity.remove(Entity.RemovalReason.KILLED);
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
    public void onServerTickPost(ServerTickEvent.Post event) {
        val serverConfig = LegendControl.getInstance().getServerConfig();

        if (!serverConfig.isLegendaryParticle() || ++this.currentTick % 40 != 0) {
            return;
        }

        val particle = (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(serverConfig.getParticleName()));

        if (particle != null) {
            for (PixelmonEntity entity : LegendControlListener.LEGENDS) {
                if (entity.level() instanceof ServerLevel level) {
                    level.sendParticles(particle, entity.getX(), entity.getYCentre(), entity.getZ(), 3,
                            level.random.nextDouble() - 0.5, level.random.nextDouble() - 0.5, level.random.nextDouble() - 0.5, 0.1
                    );
                }
            }
        }
    }
}
