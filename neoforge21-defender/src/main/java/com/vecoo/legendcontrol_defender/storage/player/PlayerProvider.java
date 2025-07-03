package com.vecoo.legendcontrol_defender.storage.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerProvider {
    Map<UUID, PlayerStorage> getMap();

    PlayerStorage getPlayerStorage(UUID uuid);

    void updatePlayerStorage(PlayerStorage storage);

    CompletableFuture<Boolean> write(PlayerStorage storage);

    void init();
}
