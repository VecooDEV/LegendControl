package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol.LegendControl;
import lombok.val;
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
        val serverConfig = LegendControl.getInstance().getServerConfig();

        if (event.phase == TickEvent.Phase.START || !serverConfig.isLegendaryParticle() || ++this.currentTick % 20 != 0) {
            return;
        }

        val particle = (BasicParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(
                new ResourceLocation(serverConfig.getParticleName())
        );

        if (particle == null) {
            return;
        }

        for (PixelmonEntity entity : LegendarySpawnListener.LEGENDS) {
            if (entity.level instanceof ServerWorld) {
                val world = (ServerWorld) entity.level;

                world.sendParticles(
                        particle, entity.getX(), entity.getYCentre(), entity.getZ(), 3,
                        world.random.nextDouble() - 0.5, world.random.nextDouble() - 0.5, world.random.nextDouble() - 0.5, 0.1
                );
            }
        }
    }
}