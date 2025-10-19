package com.vecoo.legendcontrol.mixin;

import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.spawning.SpawnAction;
import com.pixelmonmod.pixelmon.api.spawning.SpawnLocation;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.spawners.TickingSpawner;
import com.pixelmonmod.pixelmon.api.world.BlockCollection;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = LegendarySpawner.class, remap = false)
public abstract class LegendarySpawnerMixin extends TickingSpawner {
    public LegendarySpawnerMixin(String name) {
        super(name);
    }

    @Shadow
    public void forcefullySpawn(EntityPlayerMP onlyFocus) {
        throw new AssertionError();
    }

    @Shadow
    public List<SpawnAction<?>> possibleSpawns;

    @Shadow
    public boolean firesChooseEvent;

    @Shadow
    public int minDistFromCentre;

    @Shadow
    public int maxDistFromCentre;

    @Shadow
    public int horizontalSliceRadius;

    @Shadow
    public int verticalSliceRadius;

    /**
     * @author Vecoo
     * @reason Change logic spawn chance.
     */
    @Overwrite
    public List<SpawnAction<? extends Entity>> getSpawns(int pass) {
        if (pass != 0) {
            return this.possibleSpawns != null && !this.possibleSpawns.isEmpty() ? this.possibleSpawns : null;
        }

        this.possibleSpawns = null;
        int numPlayers = LegendControl.getInstance().getServer().getPlayerList().getMaxPlayers();
        int baseSpawnTicks = this.firesChooseEvent ? PixelmonConfig.legendarySpawnTicks : PixelmonConfig.bossSpawnTicks;
        this.spawnFrequency = 1200.0F / (RandomHelper.getRandomNumberBetween(0.6F, 1.4F) * baseSpawnTicks
                / (1.0F + (float) (numPlayers - 1) * PixelmonConfig.spawnTicksPlayerMultiplier));

        if (this.firesChooseEvent) {
            Utils.TIME_DO_LEGEND = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
        }

        float chance = this.firesChooseEvent ? LegendControlFactory.ServerProvider.getChanceLegend() / 100.0F : PixelmonConfig.bossSpawnChance;

        if (!RandomHelper.getRandomChance(chance)) {
            if (this.firesChooseEvent && numPlayers > 0) {
                LegendControlFactory.ServerProvider.addChanceLegend(LegendSourceName.PIXELMON, LegendControl.getInstance().getConfig().getStepSpawnChance());
            }
            return null;
        }

        if (numPlayers > 0) {
            this.forcefullySpawn(null);
        }
        return null;
    }

    @Inject(method = "forcefullySpawn", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;remove(I)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void forcefullySpawn(EntityPlayerMP onlyFocus, CallbackInfo ci, ArrayList<ArrayList<EntityPlayerMP>> clusters, ArrayList<EntityPlayerMP> players, ArrayList<EntityPlayerMP> cluster) {
        ServerConfig config = LegendControl.getInstance().getConfig();

        players.removeIf(player -> config.isBlacklistDimensions()
                && config.getBlacklistDimensionList().contains(player.getEntityWorld().provider.getDimension())
                || config.isBlacklistPlayers() && config.getBlacklistPlayersList().contains(player.getName())
        );

        if (players.isEmpty()) {
            ci.cancel();
        }
    }

    /**
     * @author Vecoo
     * @reason Fix not possibleSpawns.
     */
    @Overwrite
    public List<SpawnAction<?>> doLegendarySpawn(EntityPlayerMP target) {
        BlockCollection collection = this.getTrackedBlockCollection(target, 0.0F, 0.0F,
                this.horizontalSliceRadius, this.verticalSliceRadius, this.minDistFromCentre, this.maxDistFromCentre);
        ArrayList<SpawnLocation> spawnLocations = this.spawnLocationCalculator.calculateSpawnableLocations(collection);
        Collections.shuffle(spawnLocations);
        List<SpawnAction<?>> possibleSpawns = this.selectionAlgorithm.calculateSpawnActions(this, this.spawnSets, spawnLocations);
        if (possibleSpawns != null && !possibleSpawns.isEmpty()) {
            possibleSpawns.forEach(SpawnAction::applyLocationMutations);
            return possibleSpawns;
        } else {
            LegendControlFactory.ServerProvider.addChanceLegend(LegendSourceName.PIXELMON, LegendControl.getInstance().getConfig().getStepSpawnChance());
            return null;
        }
    }
}