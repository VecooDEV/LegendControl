package com.vecoo.legendcontrol.mixin;

import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.spawning.SpawnAction;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.spawners.TickingSpawner;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.LegendServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = LegendarySpawner.class, remap = false)
public abstract class LegendarySpawnerMixin extends TickingSpawner {

    public LegendarySpawnerMixin(String name) {
        super(name);
    }

    @Shadow
    public void forcefullySpawn(@Nullable EntityPlayerMP onlyFocus) {
        throw new AssertionError();
    }

    @Shadow
    public List<SpawnAction<?>> possibleSpawns;

    @Shadow
    public boolean firesChooseEvent;

    /**
     * @author Vecoo
     * @reason Change logic spawn chance.
     */
    @Overwrite
    public List<SpawnAction<? extends Entity>> getSpawns(int pass) {
        if (pass == 0) {
            this.possibleSpawns = null;
            int numPlayers = ExtraLib.getInstance().getServer().getPlayerList().getCurrentPlayerCount();
            int baseSpawnTicks = this.firesChooseEvent ? PixelmonConfig.legendarySpawnTicks : PixelmonConfig.bossSpawnTicks;
            int spawnTicks = (int) ((float) baseSpawnTicks / (1.0F + (float) (numPlayers - 1) * PixelmonConfig.spawnTicksPlayerMultiplier));
            this.spawnFrequency = RandomHelper.getRandomNumberBetween(0.6F, 1.4F) * (1200.0F / (float) spawnTicks);
            if (this.firesChooseEvent) {
                Utils.timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
            }
            if (this.firesChooseEvent && !RandomHelper.getRandomChance(LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance() / 100.0F) || !this.firesChooseEvent && !RandomHelper.getRandomChance(PixelmonConfig.bossSpawnChance)) {
                if (this.firesChooseEvent && ExtraLib.getInstance().getServer().getPlayerList().getCurrentPlayerCount() > 0) {
                    LegendServerFactory.addLegendaryChance(LegendControl.getInstance().getConfig().getStepSpawnChance());
                }
                return null;
            } else {
                if (ExtraLib.getInstance().getServer().getPlayerList().getCurrentPlayerCount() > 0) {
                    this.forcefullySpawn(null);
                }
                return null;
            }
        } else {
            return this.possibleSpawns != null && !this.possibleSpawns.isEmpty() ? this.possibleSpawns : null;
        }
    }
}