package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.config.SpawningGeneralConfig;
import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.api.spawning.SpawnerCoordinator;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.ExtraLib;


import java.util.ArrayList;
import java.util.Objects;

public class UtilLegendarySpawn {
    public static int countSpawn = 0;

    public static void spawn() {
        if (countSpawn >= 3) {
            return;
        }

        if (ExtraLib.getInstance().getServer().getPlayerList().getPlayerCount() == 0) {
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
                    ArrayList<String> var10000 = spawn.spawnInfo.tags;
                    SpawningGeneralConfig var10001 = PixelmonConfigProxy.getSpawning();
                    Objects.requireNonNull(var10001);
                    var10000.forEach(var10001::removeIntervalTime);
                    ExtraLib.getInstance().getServer().execute(() -> {
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