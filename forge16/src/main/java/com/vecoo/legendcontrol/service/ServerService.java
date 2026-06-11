package com.vecoo.legendcontrol.service;

import com.vecoo.extralib.loader.GsonLoader;
import com.vecoo.extralib.scheduler.TaskTimer;
import com.vecoo.extralib.util.WorldUtil;
import com.vecoo.legendcontrol.LegendControl;
import lombok.Getter;
import lombok.val;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Getter
public class ServerService {
    @Nonnull
    private final Path filePath;
    private volatile ServerStorage storage;

    @Nonnull
    private final AtomicBoolean dirty = new AtomicBoolean(false);
    @Nonnull
    private final ReentrantLock saveLock = new ReentrantLock();

    public ServerService(@Nonnull String directory, @Nonnull MinecraftServer server) {
        this.filePath = Paths.get(WorldUtil.resolveWorldDirectory(directory, server), "server_storage.json");
    }

    @Nonnull
    public ServerStorage getStorage() {
        if (this.storage == null) {
            throw new IllegalStateException("Server storage has not been initialized.");
        }

        return this.storage;
    }

    public void modifyStorage(Consumer<ServerStorage> consumer) {
        this.saveLock.lock();

        try {
            consumer.accept(getStorage());
            this.dirty.set(true);
        } finally {
            this.saveLock.unlock();
        }
    }

    public void save(boolean force) {
        this.saveLock.lock();

        try {
            if (this.dirty.compareAndSet(true, false) || force) {
                GsonLoader.save(getStorage().copy(), this.filePath);
            }
        } catch (IOException e) {
            this.dirty.set(true);
            LegendControl.getLogger().error(e.getMessage());
        } finally {
            this.saveLock.unlock();
        }
    }

    private void saveInterval() {
        TaskTimer.builder()
                .delay(315 * 20L)
                .interval(315 * 20L)
                .infinite()
                .execute(() -> {
                    if (LegendControl.getInstance().getServer().isRunning()) {
                        ServerStorage snapshot;

                        this.saveLock.lock();

                        try {
                            if (!this.dirty.compareAndSet(true, false)) {
                                return;
                            }

                            snapshot = getStorage().copy();
                        } finally {
                            this.saveLock.unlock();
                        }

                        CompletableFuture.runAsync(() -> {
                            try {
                                GsonLoader.save(snapshot, this.filePath);
                            } catch (IOException e) {
                                this.dirty.set(true);
                                LegendControl.getLogger().error("Async save error: ", e);
                            }
                        }, GsonLoader.WRITER_EXECUTOR);
                    }
                }).build();
    }

    public void init() throws IOException {
        if (this.storage != null) {
            return;
        }

        if (!Files.exists(this.filePath)) {
            this.storage = new ServerStorage(LegendControl.getInstance().getServerConfig().getBaseChance(), "None");
        } else {
            val storage = GsonLoader.load(ServerStorage.class, this.filePath, true);

            if (storage == null) {
                this.storage = new ServerStorage(LegendControl.getInstance().getServerConfig().getBaseChance(), "None");
                throw new IOException(String.format("Failed to load file: %s. Data reset, create backup.", this.filePath));
            } else {
                this.storage = storage;
            }
        }

        saveInterval();
    }
}