package com.vecoo.legendcontrol.storage;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.server.MinecraftServer;

public class ServerProvider {
    private transient final String filePath;
    private ServerStorage serverStorage;

    public ServerProvider(String filePath, MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);
    }

    public ServerStorage getServerStorage() {
        if (this.serverStorage == null) {
            this.serverStorage = new ServerStorage(LegendControl.getInstance().getConfig().getBaseChance(), "None");
        }

        return this.serverStorage;
    }

    public void updateServerStorage(ServerStorage storage) {
        this.serverStorage = storage;
    }

    public void write() {
        UtilGson.writeFileAsync(this.filePath, "ServerStorage.json", UtilGson.newGson().toJson(getServerStorage())).join();
    }

    private void writeInterval() {
        TaskTimer.builder()
                .withoutDelay()
                .interval(180 * 20L)
                .infinite()
                .consume(task -> {
                    if (LegendControl.getInstance().getServer().isRunning()) {
                        UtilGson.writeFileAsync(this.filePath, "ServerStorage.json", UtilGson.newGson().toJson(getServerStorage()));
                    }
                })
                .build();
    }

    public void init() {
        UtilGson.readFileAsync(this.filePath, "ServerStorage.json", el -> this.serverStorage = UtilGson.newGson().fromJson(el, ServerStorage.class)).join();
        writeInterval();
    }
}