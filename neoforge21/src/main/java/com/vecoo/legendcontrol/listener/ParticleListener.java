package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol.LegendControl;
import lombok.val;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class ParticleListener {
    private int currentTick = 0;

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Pre event) {
        val serverConfig = LegendControl.getInstance().getServerConfig();

        if (!serverConfig.isLegendaryParticle() || ++this.currentTick % 40 != 0) {
            return;
        }

        val particle = (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(
                ResourceLocation.parse(serverConfig.getParticleName())
        );

        if (particle == null) {
            return;
        }

        for (PixelmonEntity entity : LegendarySpawnListener.LEGENDS) {
            if (entity.level() instanceof ServerLevel level) {
                level.sendParticles(
                        particle, entity.getX(), entity.getYCentre(), entity.getZ(), 3,
                        level.random.nextDouble() - 0.5, level.random.nextDouble() - 0.5, level.random.nextDouble() - 0.5, 0.1
                );
            }
        }
    }
}