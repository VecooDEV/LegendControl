package com.vecoo.legendcontrol.mixin;

import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.spawning.SpawnAction;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.spawners.TickingSpawner;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.service.LegendControlService;
import com.vecoo.legendcontrol.util.Utils;
import lombok.val;
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
        val numPlayers = LegendControl.getInstance().getServer().getPlayerList().getMaxPlayers();
        val baseSpawnTicks = this.firesChooseEvent ? PixelmonConfig.legendarySpawnTicks : PixelmonConfig.bossSpawnTicks;
        this.spawnFrequency = 1200.0F / (RandomHelper.getRandomNumberBetween(0.6F, 1.4F) * baseSpawnTicks
                                         / (1.0F + (float) (numPlayers - 1) * PixelmonConfig.spawnTicksPlayerMultiplier));

        val serverConfig = LegendControl.getInstance().getServerConfig();

        if (this.firesChooseEvent) {
            Utils.TIME_DO_LEGEND = RandomHelper.getRandomNumberBetween(serverConfig.getRandomTimeSpawnMin(), serverConfig.getRandomTimeSpawnMax());
        }

        val chance = this.firesChooseEvent ? LegendControlService.getChanceLegend() / 100.0F : PixelmonConfig.bossSpawnChance;

        if (!RandomHelper.getRandomChance(chance)) {
            if (this.firesChooseEvent && numPlayers > 0) {
                LegendControlService.addChanceLegend(LegendSourceName.PIXELMON, serverConfig.getStepSpawnChance());
            }
            return null;
        }

        if (numPlayers > 0) {
            this.forcefullySpawn(null);
        }
        return null;
    }

    @Inject(
            method = "forcefullySpawn",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/ArrayList;remove(I)Ljava/lang/Object;"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    public void forcefullySpawn(EntityPlayerMP onlyFocus, CallbackInfo ci, ArrayList<ArrayList<EntityPlayerMP>> clusters, ArrayList<EntityPlayerMP> players, ArrayList<EntityPlayerMP> cluster) {
        val serverConfig = LegendControl.getInstance().getServerConfig();

        players.removeIf(player -> serverConfig.isBlacklistDimensions()
                                   && serverConfig.getBlacklistDimensionList().contains(player.getEntityWorld().provider.getDimension())
                                   || serverConfig.isBlacklistPlayers() && serverConfig.getBlacklistPlayersList().contains(player.getName())
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
        val collection = this.getTrackedBlockCollection(target, 0.0F, 0.0F,
                this.horizontalSliceRadius, this.verticalSliceRadius, this.minDistFromCentre, this.maxDistFromCentre);

        val spawnLocations = this.spawnLocationCalculator.calculateSpawnableLocations(collection);

        Collections.shuffle(spawnLocations);

        List<SpawnAction<?>> possibleSpawns = this.selectionAlgorithm.calculateSpawnActions(this, this.spawnSets, spawnLocations);
        if (possibleSpawns != null && !possibleSpawns.isEmpty()) {
            possibleSpawns.forEach(SpawnAction::applyLocationMutations);
            return possibleSpawns;
        } else {
            LegendControlService.addChanceLegend(LegendSourceName.PIXELMON, LegendControl.getInstance().getServerConfig().getStepSpawnChance());
            return null;
        }
    }
}