package com.vecoo.legendcontrol.storage;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.server.MinecraftServer;

public class ServerProvider {
    private transient final String filePath;
    private ServerStorage serverStorage;

    private transient boolean intervalStarted = false;
    private transient volatile boolean dirty = false;

    public ServerProvider(String filePath, MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);
    }

    public ServerStorage getStorage() {
        if (this.serverStorage == null) {
            this.serverStorage = new ServerStorage(LegendControl.getInstance().getConfig().getBaseChance(), "None");
        }

        return this.serverStorage;
    }

    public void updateServerStorage(ServerStorage storage) {
        this.serverStorage = storage;
        this.dirty = true;
    }

    public void write() {
        UtilGson.writeFileAsync(this.filePath, "ServerStorage.json", UtilGson.newGson().toJson(getStorage())).join();
    }

    private void writeInterval() {
        if (!this.intervalStarted) {
            TaskTimer.builder()
                    .withoutDelay()
                    .interval(300 * 20L)
                    .infinite()
                    .consume(task -> {
                        if (LegendControl.getInstance().getServer().isRunning() && this.dirty) {
                            UtilGson.writeFileAsync(this.filePath, "ServerStorage.json", UtilGson.newGson().toJson(getStorage())).thenRun(() -> this.dirty = false);
                        }
                    })
                    .build();

            this.intervalStarted = true;
        }
    }

    public void init() {
        UtilGson.readFileAsync(this.filePath, "ServerStorage.json", el -> this.serverStorage = UtilGson.newGson().fromJson(el, ServerStorage.class)).join();
        writeInterval();
    }
}