package com.vecoo.legendcontrol.service;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol.LegendControl;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

@Getter
public class ServerService {
    @NotNull
    private transient final String filePath;
    private ServerStorage storage;

    private transient volatile boolean dirty = false;

    public ServerService(@NotNull String filePath, @NotNull MinecraftServer server) {
        this.filePath = UtilWorld.resolveWorldDirectory(filePath, server);
    }

    @NotNull
    public ServerStorage getStorage() {
        if (this.storage == null) {
            this.storage = new ServerStorage(LegendControl.getInstance().getServerConfig().getBaseChance(), "None");
        }

        return this.storage;
    }

    public void updateStorage(@NotNull ServerStorage storage) {
        this.storage = storage;
        this.dirty = true;
    }

    public void save() {
        UtilGson.writeFileAsync(this.filePath, "server_storage.json",
                UtilGson.getGson().toJson(getStorage())).join();
    }

    private void saveInterval() {
        TaskTimer.builder()
                .withoutDelay()
                .interval(300 * 20L)
                .infinite()
                .consume(task -> {
                    if (LegendControl.getInstance().getServer().isRunning() && this.dirty) {
                        UtilGson.writeFileAsync(this.filePath, "server_storage.json",
                                UtilGson.getGson().toJson(getStorage())).thenRun(() -> this.dirty = false);
                    }
                })
                .build();
    }

    public void init() {
        UtilGson.readFileAsync(this.filePath, "server_storage.json",
                el -> this.storage = UtilGson.getGson().fromJson(el, ServerStorage.class)).join();

        saveInterval();
    }
}