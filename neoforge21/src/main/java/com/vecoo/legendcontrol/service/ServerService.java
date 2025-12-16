package com.vecoo.legendcontrol.service;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class ServerService {
    private transient final String filePath;
    private ServerStorage serverStorage;

    private transient volatile boolean dirty = false;

    public ServerService(@NotNull String filePath, @NotNull MinecraftServer server) {
        this.filePath = UtilWorld.resolveWorldDirectory(filePath, server);
    }

    @NotNull
    public ServerStorage getStorage() {
        if (this.serverStorage == null) {
            this.serverStorage = new ServerStorage(LegendControl.getInstance().getServerConfig().getBaseChance(), "None");
        }

        return this.serverStorage;
    }

    public void updateStorage(@NotNull ServerStorage serverStorage) {
        this.serverStorage = serverStorage;
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
                el -> this.serverStorage = UtilGson.getGson().fromJson(el, ServerStorage.class)).join();
        saveInterval();
    }
}