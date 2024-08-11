package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.config.BetterSpawnerConfig;
import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.api.spawning.SpawnerCoordinator;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extrasapi.ExtrasAPI;
import net.minecraftforge.fml.server.ServerLifecycleHooks;


import java.util.HashMap;

public class UtilLegendarySpawn {
    public static int countSpawn = 0;

    public static void spawn() {
        if (countSpawn >= 3) {
            return;
        }

        if (ExtrasAPI.getInstance().getServer().getPlayerList().getPlayerCount() == 0) {
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
                    ServerLifecycleHooks.getCurrentServer().execute(() -> {
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