package com.vecoo.legendcontrol.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.spawning.SpawnAction;
import com.pixelmonmod.pixelmon.api.spawning.SpawnLocation;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.spawners.TickingSpawner;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(value = LegendarySpawner.class, remap = false)
public abstract class LegendarySpawnerMixin extends TickingSpawner {
    public LegendarySpawnerMixin(String name) {
        super(name);
    }

    @Shadow
    public void forcefullySpawn(@Nullable ServerPlayer onlyFocus) {
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
        int numPlayers = LegendControl.getInstance().getServer().getPlayerList().getPlayerCount();
        int baseSpawnTicks = this.firesChooseEvent ? PixelmonConfigProxy.getSpawningLegendary().getLegendarySpawnTicks() : PixelmonConfigProxy.getSpawningBoss().getBossSpawnTicks();
        this.spawnFrequency = 1200.0F / (RandomHelper.getRandomNumberBetween(0.6F, 1.4F) * baseSpawnTicks / (1.0F + (float) (numPlayers - 1) * PixelmonConfigProxy.getSpawningLegendary().getSpawnTicksPlayerMultiplier()));

        if (this.firesChooseEvent) {
            Utils.TIME_DO_LEGEND = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
        }

        float chance = this.firesChooseEvent ? LegendControlFactory.ServerProvider.getChanceLegend() / 100.0F : PixelmonConfigProxy.getSpawningBoss().getBossSpawnChance();

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

    @Inject(method = "forcefullySpawn", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;remove(I)Ljava/lang/Object;"), cancellable = true)
    public void forcefullySpawn(ServerPlayer onlyFocus, CallbackInfo ci, @Local(ordinal = 1) ArrayList<ServerPlayer> players) {
        ServerConfig config = LegendControl.getInstance().getConfig();

        players.removeIf(player -> config.isBlacklistDimensions() && config.getBlacklistDimensionList().contains(player.level().dimension().location().getPath()) || config.isBlacklistPlayers() && config.getBlacklistPlayersList().contains(player.getName().getString()));

        if (players.isEmpty()) {
            ci.cancel();
        }
    }

    /**
     * @author Vecoo
     * @reason Fix not possibleSpawns.
     */
    @Overwrite
    public CompletableFuture<List<SpawnAction<?>>> doLegendarySpawn(ServerPlayer target) {
        return target == null ? CompletableFuture.completedFuture(Collections.emptyList()) : this.getTrackedBlockCollection(target, 0.0F, 0.0F, horizontalSliceRadius, this.verticalSliceRadius, this.minDistFromCentre, this.maxDistFromCentre).thenApply((blockCollection) -> {
            ArrayList<SpawnLocation> spawnLocations = this.spawnLocationCalculator.calculateSpawnableLocations(blockCollection);
            Collections.shuffle(spawnLocations);
            List<SpawnAction<?>> possibleSpawns = this.selectionAlgorithm.calculateSpawnActions(this, this.spawnSets, spawnLocations);

            if (possibleSpawns != null && !possibleSpawns.isEmpty()) {
                possibleSpawns.forEach(SpawnAction::applyLocationMutations);
                return possibleSpawns;
            } else {
                LegendControlFactory.ServerProvider.addChanceLegend(LegendSourceName.PIXELMON, LegendControl.getInstance().getConfig().getStepSpawnChance());
                return Collections.emptyList();
            }
        });
    }
}