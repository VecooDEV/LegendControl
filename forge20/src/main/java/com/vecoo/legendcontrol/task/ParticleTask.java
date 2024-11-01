package com.vecoo.legendcontrol.task;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Iterator;
import java.util.List;

public class ParticleTask {
    private static ParticleTask instance;

    private final List<PixelmonEntity> huntPokemon = Lists.newArrayList();

    private int currentTick = 0;

    public ParticleTask() {
        super();

        instance = this;
    }

    public static void addPokemon(PixelmonEntity pixelmon) {
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

        Iterator<PixelmonEntity> iterator = this.huntPokemon.iterator();

        while (iterator.hasNext()) {
            PixelmonEntity pixelmon = iterator.next();

            if (pixelmon == null || !pixelmon.isAlive() || pixelmon.hasOwner()) {
                iterator.remove();
                continue;
            }

            ((ServerLevel) pixelmon.level()).sendParticles(ParticleTypes.DRAGON_BREATH,
                    pixelmon.getX(), pixelmon.getY(), pixelmon.getZ(),
                    1, pixelmon.level().random.nextDouble() - 0.5, pixelmon.level().random.nextDouble() - 0.5, pixelmon.level().random.nextDouble() - 0.5, 1);
        }
    }
}