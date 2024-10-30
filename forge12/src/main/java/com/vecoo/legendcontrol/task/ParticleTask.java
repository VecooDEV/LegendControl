package com.vecoo.legendcontrol.task;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.List;

public class ParticleTask {
    private static ParticleTask instance;

    private final List<EntityPixelmon> huntPokemon = Lists.newArrayList();

    private int currentTick = 0;

    public ParticleTask() {
        super();

        instance = this;
    }

    public static void addPokemon(EntityPixelmon pixelmon) {
        instance.huntPokemon.add(pixelmon);
    }


    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (!LegendControl.getInstance().getConfig().isLegendaryParticle()) {
            return;
        }

        ++this.currentTick;

        if (this.currentTick % 20 != 0) {
            return;
        }

        Iterator<EntityPixelmon> iterator = this.huntPokemon.iterator();

        while (iterator.hasNext()) {
            EntityPixelmon pixelmon = iterator.next();

            if (pixelmon == null || pixelmon.isDead || pixelmon.hasOwner()) {
                iterator.remove();
                continue;
            }

            WorldServer worldServer = (WorldServer) pixelmon.getEntityWorld();
            Vec3d positionVector = pixelmon.getPositionVector();

            worldServer.spawnParticle(EnumParticleTypes.DRAGON_BREATH,
                    positionVector.x, positionVector.y, positionVector.z, 5,
                    0, 0, 0, 0.05);
        }
    }
}