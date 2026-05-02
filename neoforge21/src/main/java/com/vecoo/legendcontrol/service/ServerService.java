package com.vecoo.legendcontrol.service;

import com.vecoo.extralib.loader.GsonLoader;
import com.vecoo.extralib.scheduler.TaskTimer;
import com.vecoo.extralib.util.WorldUtil;
import com.vecoo.legendcontrol.LegendControl;
import lombok.Getter;
import lombok.val;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Getter
public class ServerService {
    @NotNull
    private final Path filePath;
    private volatile ServerStorage storage;

    @NotNull
    private final AtomicBoolean dirty = new AtomicBoolean(false);

    public ServerService(@NotNull String directory, @NotNull MinecraftServer server) {
        this.filePath = Path.of(WorldUtil.resolveWorldDirectory(directory, server), "server_storage.json");
    }

    @NotNull
    public ServerStorage getStorage() {
        if (this.storage == null) {
            throw new IllegalStateException("Server storage has not been initialized.");
        }

        return this.storage;
    }

    public void modifyStorage(Consumer<ServerStorage> consumer) {
        consumer.accept(getStorage());
        this.dirty.set(true);
    }

    public void save() {
        this.dirty.set(false);

        val snapshot = getStorage().copy();

        try {
            GsonLoader.save(snapshot, this.filePath);
        } catch (IOException e) {
            this.dirty.set(true);
            LegendControl.getLogger().error(e.getMessage());
        }
    }

    private void saveInterval() {
        TaskTimer.builder()
                .delay(300 * 20L)
                .interval(300 * 20L)
                .infinite()
                .execute(() -> {
                    if (LegendControl.getInstance().getServer().isRunning() && this.dirty.compareAndSet(true, false)) {
                        val snapshot = getStorage().copy();

                        GsonLoader.saveAsync(snapshot, this.filePath)
                                .exceptionally(e -> {
                                    this.dirty.set(true);
                                    LegendControl.getLogger().error("Async save error: ", e);
                                    return null;
                                });
                    }
                })
                .build();
    }

    public void init() throws IOException {
        if (this.storage != null) {
            return;
        }

        if (!Files.exists(this.filePath)) {
            this.storage = new ServerStorage(LegendControl.getInstance().getServerConfig().getBaseChance(), "None");
            save();
        } else {
            val storage = GsonLoader.load(ServerStorage.class, this.filePath, true);

            if (storage == null) {
                this.storage = new ServerStorage(LegendControl.getInstance().getServerConfig().getBaseChance(), "None");
                save();

                throw new IOException(String.format("Failed to load file: %s. Data reset, create backup.", this.filePath));
            } else {
                this.storage = storage;
            }
        }

        saveInterval();
    }
}