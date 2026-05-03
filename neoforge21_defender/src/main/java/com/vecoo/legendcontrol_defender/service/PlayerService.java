package com.vecoo.legendcontrol_defender.service;

import com.vecoo.extralib.loader.GsonLoader;
import com.vecoo.extralib.scheduler.TaskTimer;
import com.vecoo.extralib.util.WorldUtil;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import lombok.Getter;
import lombok.val;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Getter
public class PlayerService {
    @NotNull
    private final Path filePath;
    @NotNull
    private final Map<UUID, PlayerStorage> storage = new ConcurrentHashMap<>();
    @NotNull
    private final Map<UUID, ReentrantLock> playerLocks = new ConcurrentHashMap<>();

    public PlayerService(@NotNull String directory, @NotNull MinecraftServer server) {
        this.filePath = Path.of(WorldUtil.resolveWorldDirectory(directory, server));
    }

    @NotNull
    public PlayerStorage getStorage(@NotNull UUID playerUUID) {
        return this.storage.computeIfAbsent(playerUUID, uuid ->
                new PlayerStorage(uuid, new LinkedHashSet<>())
        );
    }

    public void modifyStorage(@NotNull UUID playerUUID, Consumer<PlayerStorage> consumer) {
        val storage = getStorage(playerUUID);
        val lock = getLock(playerUUID);

        lock.lock();

        try {
            consumer.accept(storage);
            storage.getDirty().set(true);
        } finally {
            lock.unlock();
        }
    }

    public void save(boolean force) {
        for (PlayerStorage storage : this.storage.values()) {
            val lock = getLock(storage.getPlayerUUID());

            lock.lock();

            try {
                if (storage.getDirty().compareAndSet(true, false) || force) {
                    GsonLoader.save(storage.copy(), this.filePath.resolve(storage.getPlayerUUID() + ".json"));
                }
            } catch (IOException e) {
                storage.getDirty().set(true);
                LegendControlDefender.getLogger().error(e.getMessage());
            } finally {
                lock.unlock();
            }
        }
    }

    private void saveInterval() {
        TaskTimer.builder()
                .delay(315 * 20L)
                .interval(315 * 20L)
                .infinite()
                .execute(() -> {
                    if (LegendControlDefender.getInstance().getServer().isRunning()) {
                        for (PlayerStorage storage : this.storage.values()) {
                            PlayerStorage snapshot;
                            val lock = getLock(storage.getPlayerUUID());

                            lock.lock();

                            try {
                                if (!storage.getDirty().compareAndSet(true, false)) {
                                    continue;
                                }

                                snapshot = storage.copy();
                            } finally {
                                lock.unlock();
                            }

                            CompletableFuture.runAsync(() -> {
                                try {
                                    GsonLoader.save(snapshot, this.filePath.resolve(snapshot.getPlayerUUID() + ".json"));
                                } catch (IOException e) {
                                    storage.getDirty().set(true);
                                    LegendControlDefender.getLogger().error("Async save error: ", e);
                                }
                            }, GsonLoader.WRITER_EXECUTOR);
                        }
                    }
                }).build();
    }

    public void init() throws IOException {
        if (!this.storage.isEmpty()) {
            return;
        }

        val list = this.filePath.toFile().listFiles((dir, name) -> name.endsWith(".json"));

        if (list == null) {
            return;
        }

        for (File file : list) {
            val storage = GsonLoader.load(PlayerStorage.class, file.toPath(), true);

            if (storage == null) {
                throw new IOException(String.format("Failed to load file: %s. Data reset, create backup.", file.toPath()));
            } else {
                this.storage.put(storage.getPlayerUUID(), storage);
            }
        }

        saveInterval();
    }

    @NotNull
    private ReentrantLock getLock(@NotNull UUID playerUUID) {
        return this.playerLocks.computeIfAbsent(playerUUID, uuid -> new ReentrantLock());
    }
}