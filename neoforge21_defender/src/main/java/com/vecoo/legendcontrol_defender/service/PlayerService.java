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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
public class PlayerService {
    @NotNull
    private final Path filePath;
    @NotNull
    private final Map<UUID, PlayerStorage> storage;

    public PlayerService(@NotNull String directory, @NotNull MinecraftServer server) {
        this.filePath = Path.of(WorldUtil.resolveWorldDirectory(directory, server));

        this.storage = new ConcurrentHashMap<>();
    }

    @NotNull
    public PlayerStorage getStorage(@NotNull UUID playerUUID) {
        return this.storage.computeIfAbsent(playerUUID, uuid ->
                new PlayerStorage(uuid, new LinkedHashSet<>(), true)
        );
    }

    public void modifyStorage(@NotNull UUID playerUUID, Consumer<PlayerStorage> consumer) {
        val storage = getStorage(playerUUID);

        storage.setDirty(true);
        consumer.accept(storage);
    }

    public void save() {
        for (PlayerStorage storage : this.storage.values()) {
            try {
                GsonLoader.save(getStorage(), this.filePath.resolve(storage.getPlayerUUID() + ".json"));
            } catch (IOException e) {
                storage.setDirty(false);
                LegendControlDefender.getLogger().error(e.getMessage());
            }
        }
    }

    private void saveInterval() {
        TaskTimer.builder()
                .delay(325 * 20L)
                .interval(325 * 20L)
                .infinite()
                .execute(() -> {
                    if (LegendControlDefender.getInstance().getServer().isRunning()) {
                        for (PlayerStorage storage : this.storage.values()) {
                            if (storage.isDirty()) {
                                val snapshot = storage.copy();

                                GsonLoader.saveAsync(snapshot, this.filePath.resolve(snapshot.getPlayerUUID() + ".json"))
                                        .exceptionally(e -> {
                                            snapshot.setDirty(true);
                                            LegendControlDefender.getLogger().error("Async save error: ", e);
                                            return null;
                                        });
                            }
                        }
                    }
                })
                .build();
    }

    public void init() throws IOException {
        if (!this.storage.isEmpty()) {
            return;
        }

        val list = filePath.toFile().listFiles((dir, name) -> name.endsWith(".json"));

        if (list == null) {
            return;
        }

        for (File file : list) {
            val storage = GsonLoader.load(PlayerStorage.class, file.toPath(), true);

            if (storage != null) {
                storage.setDirty(false);
                this.storage.put(storage.getPlayerUUID(), storage);
            }
        }

        saveInterval();
    }
}