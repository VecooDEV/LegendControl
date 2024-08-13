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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
    public static void fillNearby(ArrayList<ServerPlayer> allPlayers, ArrayList<ServerPlayer> cluster, ServerPlayer focus) {
        throw new AssertionError();
    }

    @Shadow
    public CompletableFuture<List<SpawnAction<?>>> doLegendarySpawn(ServerPlayer target) {
        throw new AssertionError();
    }

    @Shadow
    private ServerPlayer getPlayer(ServerPlayer onlyFocus, ArrayList<ArrayList<ServerPlayer>> clusters) {
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
    public void forcefullySpawn(@Nullable ServerPlayer onlyFocus) {
        ArrayList<ArrayList<ServerPlayer>> clusters = new ArrayList<>();
        ArrayList<ServerPlayer> players = new ArrayList<>(ExtraLib.getInstance().getServer().getPlayerList().getPlayers());
        if (onlyFocus == null) {
            while (!players.isEmpty()) {
                ArrayList<ServerPlayer> cluster = new ArrayList<>();
                ServerPlayer focus = players.remove(0);
                if (!PixelmonConfigProxy.getSpawning().isSpawningDisabledDimension(focus.level())) {
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
        ServerPlayer spawnedOnPlayer = this.getPlayer(onlyFocus, clusters);
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
            int baseSpawnTicks = this.firesChooseEvent ? PixelmonConfigProxy.getSpawningLegendary().getLegendarySpawnTicks() : PixelmonConfigProxy.getSpawningBoss().getBossSpawnTicks();
            int spawnTicks = (int) ((float) baseSpawnTicks / (1.0F + (float) (numPlayers - 1) * PixelmonConfigProxy.getSpawningLegendary().getSpawnTicksPlayerMultiplier()));
            this.spawnFrequency = 1200.0F / (RandomHelper.getRandomNumberBetween(0.6F, 1.4F) * (float) spawnTicks);
            if (this.firesChooseEvent) {
                Utils.timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
            }
            if (this.firesChooseEvent && !RandomHelper.getRandomChance(LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance() / 100.0F) || !this.firesChooseEvent && !RandomHelper.getRandomChance(PixelmonConfigProxy.getSpawningBoss().getBossSpawnChance())) {
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