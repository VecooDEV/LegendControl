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
        if (event.phase == TickEvent.Phase.START || !LegendControl.getInstance().getConfig().isLegendaryParticle() || ++this.currentTick % 20 != 0) {
            return;
        }

        BasicParticleType particle = (BasicParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(
                new ResourceLocation(LegendControl.getInstance().getConfig().getParticleName()));

        if (particle == null) {
            return;
        }

        for (PixelmonEntity entity : LegendarySpawnListener.LEGENDS) {
            if (entity.level instanceof ServerWorld) {
                ServerWorld world = (ServerWorld) entity.level;

                world.sendParticles(
                        particle, entity.getX(), entity.getYCentre(), entity.getZ(), 3,
                        world.random.nextDouble() - 0.5, world.random.nextDouble() - 0.5, world.random.nextDouble() - 0.5, 0.1
                );
            }
        }
    }
}