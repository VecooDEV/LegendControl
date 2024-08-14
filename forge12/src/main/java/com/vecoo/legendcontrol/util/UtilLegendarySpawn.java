package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.api.spawning.SpawnerCoordinator;
import com.pixelmonmod.pixelmon.config.BetterSpawnerConfig;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.ExtraLib;

import java.util.HashMap;

public class UtilLegendarySpawn {
    public static int countSpawn = 0;

    public static void spawn() {
        if (countSpawn >= 3) {
            return;
        }

        if (ExtraLib.getInstance().getServer().getPlayerList().getCurrentPlayerCount() == 0) {
            return;
        }

        AbstractSpawner abstrSpawner = PixelmonSpawning.coordinator.getSpawner("legendary");

        if (abstrSpawner == null) {
            abstrSpawner = PixelmonSpawning.legendarySpawner;
        }

        boolean isCoordinatorActive = PixelmonSpawning.coordinator.getActive();
        LegendarySpawner legendarySpawner = (LegendarySpawner) abstrSpawner;

        if (isCoordinatorActive) {
            legendarySpawner.forcefullySpawn(null);
        }

        Runnable r = () -> {
            if (legendarySpawner.possibleSpawns != null && !legendarySpawner.possibleSpawns.isEmpty()) {
                legendarySpawner.possibleSpawns.forEach((spawn) -> {
                    HashMap<String, Long> var10001 = BetterSpawnerConfig.intervalTimes;
                    spawn.spawnInfo.tags.forEach(var10001::remove);
                    ExtraLib.getInstance().getServer().addScheduledTask(() -> {
                        spawn.doSpawn(legendarySpawner);
                    });
                });
                legendarySpawner.possibleSpawns = null;
            }
        };
        if (isCoordinatorActive) {
            SpawnerCoordinator.PROCESSOR.execute(r);
        }
        countSpawn++;
    }
}