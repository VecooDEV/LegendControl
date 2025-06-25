package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol.LegendControl;
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
        if (!LegendControl.getInstance().getConfig().isLegendaryParticle() || ++this.currentTick % 40 != 0) {
            return;
        }

        SimpleParticleType particle = (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(LegendControl.getInstance().getConfig().getParticleName()));

        if (particle == null) {
            return;
        }

        LegendarySpawnListener.getLegends().removeIf(entity -> entity == null || !entity.isAlive() || entity.hasOwner());

        for (PixelmonEntity entity : LegendarySpawnListener.getLegends()) {
            if (entity.level() instanceof ServerLevel level) {
                level.sendParticles(particle, entity.getX(), entity.getYCentre(), entity.getZ(), 3, level.random.nextDouble() - 0.5, level.random.nextDouble() - 0.5, level.random.nextDouble() - 0.5, 0.1);
            }
        }
    }
}