package com.vecoo.legendcontrol.storage;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class ServerProvider {
    private transient final String filePath;
    private ServerStorage serverStorage;

    private transient boolean intervalStarted = false;
    private transient volatile boolean dirty = false;

    public ServerProvider(@NotNull String filePath, @NotNull MinecraftServer server) {
        this.filePath = UtilWorld.resolveWorldDirectory(filePath, server);
    }

    @NotNull
    public ServerStorage getServerStorage() {
        if (this.serverStorage == null) {
            this.serverStorage = new ServerStorage(LegendControl.getInstance().getConfig().getBaseChance(), "None");
        }

        return this.serverStorage;
    }

    public void updateServerStorage(@NotNull ServerStorage serverStorage) {
        this.serverStorage = serverStorage;
        this.dirty = true;
    }

    public void save() {
        UtilGson.writeFileAsync(this.filePath, "server_storage.json", UtilGson.getGson().toJson(getServerStorage())).join();
    }

    private void saveInterval() {
        if (!this.intervalStarted) {
            TaskTimer.builder()
                    .withoutDelay()
                    .interval(180 * 20L)
                    .infinite()
                    .consume(task -> {
                        if (LegendControl.getInstance().getServer().isRunning() && this.dirty) {
                            UtilGson.writeFileAsync(this.filePath, "server_storage.json",
                                    UtilGson.getGson().toJson(getServerStorage())).thenRun(() -> this.dirty = false);
                        }
                    })
                    .build();

            this.intervalStarted = true;
        }
    }

    public void init() {
        UtilGson.readFileAsync(this.filePath, "server_storage.json",
                el -> this.serverStorage = UtilGson.getGson().fromJson(el, ServerStorage.class)).join();

        saveInterval();
    }
}