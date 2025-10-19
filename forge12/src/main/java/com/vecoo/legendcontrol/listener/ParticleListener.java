package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ParticleListener {
    private int currentTick = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || !LegendControl.getInstance().getConfig().isLegendaryParticle() || ++this.currentTick % 20 != 0) {
            return;
        }

        EnumParticleTypes particle = EnumParticleTypes.getByName(LegendControl.getInstance().getConfig().getParticleName());

        if (particle == null) {
            return;
        }

        LegendarySpawnListener.LEGENDS.removeIf(entity -> entity == null || !entity.isEntityAlive() || entity.hasOwner());

        for (EntityPixelmon entity : LegendarySpawnListener.LEGENDS) {
            if (entity.world instanceof WorldServer) {
                WorldServer world = (WorldServer) entity.world;

                world.spawnParticle(particle, entity.posX, entity.getYCentre(), entity.posZ, 3,
                        world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 0.5, 0.1);
            }
        }
    }
}