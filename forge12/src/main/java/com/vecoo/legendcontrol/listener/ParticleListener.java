package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.legendcontrol.LegendControl;
import lombok.val;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ParticleListener {
    private int currentTick = 0;

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

        LegendarySpawnListener.LEGENDS.removeIf(
                entity -> entity == null || !entity.isEntityAlive() || entity.hasOwner()
        );

        for (EntityPixelmon entity : LegendarySpawnListener.LEGENDS) {
            if (entity.world instanceof WorldServer) {
                val world = (WorldServer) entity.world;

                world.spawnParticle(particle, entity.posX, entity.getYCentre(), entity.posZ, 3,
                        world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 0.5, 0.1);
            }
        }
    }
}