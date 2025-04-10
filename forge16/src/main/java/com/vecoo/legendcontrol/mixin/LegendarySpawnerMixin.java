package com.vecoo.legendcontrol.mixin;

import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.spawning.SpawnAction;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.spawners.TickingSpawner;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = LegendarySpawner.class, remap = false)
public abstract class LegendarySpawnerMixin extends TickingSpawner {
    public LegendarySpawnerMixin(String name) {
        super(name);
    }

    @Shadow
    public void forcefullySpawn(@Nullable ServerPlayerEntity onlyFocus) {
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
        if (pass != 0) {
            return this.possibleSpawns != null && !this.possibleSpawns.isEmpty() ? this.possibleSpawns : null;
        }

        this.possibleSpawns = null;
        int numPlayers = LegendControl.getInstance().getServer().getPlayerList().getPlayerCount();
        int baseSpawnTicks = this.firesChooseEvent ? PixelmonConfigProxy.getSpawning().getLegendarySpawnTicks() : PixelmonConfigProxy.getSpawning().getBossSpawning().getBossSpawnTicks();
        this.spawnFrequency = 1200.0F / (RandomHelper.getRandomNumberBetween(0.6F, 1.4F) * baseSpawnTicks / (1.0F + (float) (numPlayers - 1) * PixelmonConfigProxy.getSpawning().getSpawnTicksPlayerMultiplier()));

        if (this.firesChooseEvent) {
            Utils.timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
        }

        float chance = this.firesChooseEvent ? LegendControlFactory.ServerProvider.getLegendaryChance() / 100.0F : PixelmonConfigProxy.getSpawning().getBossSpawning().getBossSpawnChance();

        if (!RandomHelper.getRandomChance(chance)) {
            if (this.firesChooseEvent && numPlayers > 0) {
                LegendControlFactory.ServerProvider.addLegendaryChance(LegendControl.getInstance().getConfig().getStepSpawnChance());
            }
            return null;
        }

        if (numPlayers > 0) {
            this.forcefullySpawn(null);
        }
        return null;
    }

    @Inject(method = "forcefullySpawn", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;remove(I)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void forcefullySpawn(ServerPlayerEntity onlyFocus, CallbackInfo ci, ArrayList<ArrayList<ServerPlayerEntity>> clusters, ArrayList<ServerPlayerEntity> players, ArrayList<ServerPlayerEntity> cluster) {
        ServerConfig config = LegendControl.getInstance().getConfig();

        players.removeIf(player -> config.isBlacklistDimensions() && config.getBlacklistDimensionList().contains(player.getLevel().dimension().location().getPath()));

        if (players.isEmpty()) {
            ci.cancel();
        }
    }
}