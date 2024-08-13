package com.vecoo.legendcontrol.mixin;

import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.spawning.SpawnAction;
import com.pixelmonmod.pixelmon.api.spawning.SpawnerCoordinator;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.spawners.TickingSpawner;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(value = LegendarySpawner.class, remap = false)
public abstract class LegendarySpawnerMixin extends TickingSpawner {

    public LegendarySpawnerMixin(String name) {
        super(name);
    }

    @Shadow
    public static void fillNearby(ArrayList<ServerPlayerEntity> allPlayers, ArrayList<ServerPlayerEntity> cluster, ServerPlayerEntity focus) {
        throw new AssertionError();
    }

    @Shadow
    public CompletableFuture<List<SpawnAction<?>>> doLegendarySpawn(ServerPlayerEntity target) {
        throw new AssertionError();
    }

    @Shadow
    private ServerPlayerEntity getPlayer(ServerPlayerEntity onlyFocus, ArrayList<ArrayList<ServerPlayerEntity>> clusters) {
        throw new AssertionError();
    }

    @Shadow
    public List<SpawnAction<?>> possibleSpawns;

    @Shadow
    public boolean firesChooseEvent;

    /**
     * @author Vecoo
     * @reason Change logic choose player.
     */
    @Overwrite
    public void forcefullySpawn(@Nullable ServerPlayerEntity onlyFocus) {
        ArrayList<ArrayList<ServerPlayerEntity>> clusters = new ArrayList<>();
        ArrayList<ServerPlayerEntity> players = new ArrayList<>(ExtraLib.getInstance().getServer().getPlayerList().getPlayers());
        if (onlyFocus == null) {
            while (!players.isEmpty()) {
                ArrayList<ServerPlayerEntity> cluster = new ArrayList<>();
                ServerPlayerEntity focus = players.remove(0);
                if (!PixelmonConfigProxy.getSpawning().isSpawningDisabledDimension(focus.getLevel())) {
                    if (this.firesChooseEvent) {
                        if (!ServerFactory.getPlayersBlacklist().contains(focus.getUUID())) {
                            Utils.updatePlayerIP(focus);
                            if (!ServerFactory.getPlayersIP().containsValue(focus.getIpAddress()) || LegendControl.getInstance().getConfig().getMaxPlayersIP() == 0) {
                                if (Utils.playerCountIP(focus) <= LegendControl.getInstance().getConfig().getLockPlayerIP() || LegendControl.getInstance().getConfig().getLockPlayerIP() == 0) {
                                    cluster.add(focus);
                                    fillNearby(players, cluster, focus);
                                    clusters.add(cluster);
                                }
                            }
                        }
                    } else {
                        cluster.add(focus);
                        fillNearby(players, cluster, focus);
                        clusters.add(cluster);
                    }
                }
            }
        }

        this.isBusy = true;
        ServerPlayerEntity spawnedOnPlayer = this.getPlayer(onlyFocus, clusters);
        this.doLegendarySpawn(spawnedOnPlayer).whenCompleteAsync((spawnActions, throwable) -> {
            this.possibleSpawns = spawnActions;
            this.isBusy = false;
        }, SpawnerCoordinator.PROCESSOR);
    }

    /**
     * @author Vecoo
     * @reason Change logic spawn chance.
     */
    @Overwrite
    public List<SpawnAction<? extends Entity>> getSpawns(int pass) {
        if (pass == 0) {
            this.possibleSpawns = null;
            int numPlayers = ExtraLib.getInstance().getServer().getPlayerList().getPlayerCount();
            int baseSpawnTicks = this.firesChooseEvent ? PixelmonConfigProxy.getSpawning().getLegendarySpawnTicks() : PixelmonConfigProxy.getSpawning().getBossSpawning().getBossSpawnTicks();
            int spawnTicks = (int) ((float) baseSpawnTicks / (1.0F + (float) (numPlayers - 1) * PixelmonConfigProxy.getSpawning().getSpawnTicksPlayerMultiplier()));
            this.spawnFrequency = 1200.0F / (RandomHelper.getRandomNumberBetween(0.6F, 1.4F) * (float) spawnTicks);
            if (this.firesChooseEvent) {
                Utils.timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
            }
            if (this.firesChooseEvent && !RandomHelper.getRandomChance(LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance() / 100.0F) || !this.firesChooseEvent && !RandomHelper.getRandomChance(PixelmonConfigProxy.getSpawning().getBossSpawning().getBossSpawnChance())) {
                if (this.firesChooseEvent) {
                    ServerFactory.addLegendaryChance(LegendControl.getInstance().getConfig().getStepSpawnChance());
                }
                return null;
            } else {
                if (ExtraLib.getInstance().getServer().getPlayerList().getPlayerCount() > 0) {
                    this.forcefullySpawn(null);
                }
                return null;
            }
        } else {
            return this.possibleSpawns != null && !this.possibleSpawns.isEmpty() ? this.possibleSpawns : null;
        }
    }
}