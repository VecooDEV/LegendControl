package com.vecoo.legendcontrol.task;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;
import java.util.List;

public class ParticleTask {
    private static ParticleTask instance;

    private final List<PixelmonEntity> legenaryPokemon = Lists.newArrayList();

    private int currentTick = 0;

    public ParticleTask() {
        super();

        instance = this;
    }

    public static void addPokemon(PixelmonEntity pixelmon) {
        instance.legenaryPokemon.add(pixelmon);
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

        Iterator<PixelmonEntity> iterator = this.legenaryPokemon.iterator();

        while (iterator.hasNext()) {
            PixelmonEntity pixelmon = iterator.next();

            if (pixelmon == null || !pixelmon.isAlive() || pixelmon.hasOwner()) {
                iterator.remove();
                continue;
            }

            SimpleParticleType particle = (SimpleParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(LegendControl.getInstance().getConfig().getParticleName()));

            if (particle != null) {
                ((ServerLevel) pixelmon.level()).sendParticles(particle,
                        pixelmon.getX(), pixelmon.getY(), pixelmon.getZ(),
                        1, pixelmon.level().random.nextDouble() - 0.5, pixelmon.level().random.nextDouble() - 0.5, pixelmon.level().random.nextDouble() - 0.5, 1);
            }
        }
    }
}