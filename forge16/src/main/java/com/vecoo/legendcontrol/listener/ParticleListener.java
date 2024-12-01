package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleListener {
    private int currentTick = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (!LegendControl.getInstance().getConfig().isLegendaryParticle()) {
            return;
        }

        ++this.currentTick;

        if (this.currentTick % 20 != 0) {
            return;
        }

        for (PixelmonEntity entity : LegendaryListener.playerEntity.keySet()) {
            if (entity == null || !entity.isAlive() || entity.hasOwner()) {
                continue;
            }

            ServerWorld world = (ServerWorld) entity.level;

            BasicParticleType particle = (BasicParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(LegendControl.getInstance().getConfig().getParticleName()));

            if (particle != null) {
                world.sendParticles(particle, entity.getX(), entity.getY(), entity.getZ(),
                        1, entity.level.random.nextDouble() - 0.5, entity.level.random.nextDouble() - 0.5, entity.level.random.nextDouble() - 0.5, 1);
            }
        }
    }
}