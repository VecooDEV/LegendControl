package com.vecoo.legendcontrol.storage.server;

import java.util.concurrent.CompletableFuture;

public interface ServerProvider {
    ServerStorage getServerStorage();

    void updateServerStorage(ServerStorage storage);

    CompletableFuture<Boolean> write(ServerStorage storage);

    void init();
}