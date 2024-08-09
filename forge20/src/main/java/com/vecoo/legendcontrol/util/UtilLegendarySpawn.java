package com.vecoo.legendcontrol.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.config.SpawningGeneralConfig;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.api.spawning.SpawnerCoordinator;
import com.pixelmonmod.pixelmon.api.util.helpers.CollectionHelper;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UtilLegendarySpawn {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor((new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("pixelmon_legendary_spawn_%d").build());

    public static void spawn() {
        if (LegendControl.getInstance().getServer().getPlayerList().getPlayerCount() == 0) {
            return;
        }

        Collection<ServerPlayer> players = LegendControl.getInstance().getServer().getPlayerList().getPlayers();
        ServerPlayer target = RandomHelper.getRandomElementFromList(players);

        boolean isCoordinatorActive = PixelmonSpawning.coordinator.getActive();
        AbstractSpawner abstrSpawner = PixelmonSpawning.coordinator.getSpawner("legendary");
        if (abstrSpawner == null) {
            abstrSpawner = PixelmonSpawning.legendarySpawner;
        }

        LegendarySpawner spawner = (LegendarySpawner) PixelmonCommandUtils.require(abstrSpawner, "spawning.error.legendaryspawnermissing", new Object[0]);
        if (isCoordinatorActive) {
            spawner.forcefullySpawn(target);
        } else {
            forcefullySpawn(target, spawner);
        }

        Runnable r = () -> {
            if (spawner.possibleSpawns != null && !spawner.possibleSpawns.isEmpty()) {
                spawner.possibleSpawns.forEach((spawn) -> {
                    ArrayList<String> var10000 = spawn.spawnInfo.tags;
                    SpawningGeneralConfig var10001 = PixelmonConfigProxy.getSpawning();
                    Objects.requireNonNull(var10001);
                    var10000.forEach(var10001::removeIntervalTime);
                    LegendControl.getInstance().getServer().execute(() -> {
                        spawn.doSpawn(spawner);
                    });
                });
                spawner.possibleSpawns = null;
            }

        };
        if (isCoordinatorActive) {
            SpawnerCoordinator.PROCESSOR.execute(r);
        } else {
            EXECUTOR_SERVICE.execute(r);
        }
    }

    private static void forcefullySpawn(@Nullable ServerPlayer onlyFocus, LegendarySpawner spawner) {
        ArrayList<ArrayList<ServerPlayer>> clusters = new ArrayList<>();
        ArrayList<ServerPlayer> players = new ArrayList<>(LegendControl.getInstance().getServer().getPlayerList().getPlayers());
        if (onlyFocus == null) {
            while (!players.isEmpty()) {
                ArrayList<ServerPlayer> cluster = new ArrayList<>();
                ServerPlayer focus = players.remove(0);
                cluster.add(focus);
                LegendarySpawner.fillNearby(players, cluster, focus);
                clusters.add(cluster);
            }
        }

        spawner.isBusy = true;
        EXECUTOR_SERVICE.execute(() -> {
            if (onlyFocus != null) {
                spawner.possibleSpawns = spawner.doLegendarySpawn(onlyFocus).join();
            } else {
                Collections.shuffle(clusters);

                while (clusters.size() > 0) {
                    for (int i = 0; i < clusters.size(); ++i) {
                        ServerPlayer player = CollectionHelper.getRandomElement(clusters.get(i));
                        (clusters.get(i)).remove(player);
                        if ((clusters.get(i)).isEmpty()) {
                            clusters.remove(i--);
                        }

                        if (spawner.firesChooseEvent) {
                            LegendarySpawnEvent.ChoosePlayer event = new LegendarySpawnEvent.ChoosePlayer(spawner, player, clusters);
                            if (Pixelmon.EVENT_BUS.post(event) || event.player == null) {
                                continue;
                            }

                            player = event.player;
                        }

                        spawner.possibleSpawns = spawner.doLegendarySpawn(player).join();
                        if (spawner.possibleSpawns != null) {
                            spawner.isBusy = false;
                            return;
                        }
                    }
                }
            }

            spawner.isBusy = false;
        });
    }
}