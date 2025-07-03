package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.events.LegendControlEvent;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.storage.server.ServerStorage;
import com.vecoo.legendcontrol.util.WebhookUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class LegendarySpawnListener {
    public static final List<PixelmonEntity> LEGENDS = new ArrayList<>();

    public static List<PixelmonEntity> getLegends() {
        return LEGENDS;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        PixelmonEntity pixelmonEntity = event.action.getOrCreateEntity();
        ServerPlayer player = (ServerPlayer) event.action.spawnLocation.cause;
        ServerConfig config = LegendControl.getInstance().getConfig();

        if (!config.isLegendaryRepeat() && LegendControlFactory.ServerProvider.getLastLegend().equals(pixelmonEntity.getPokemonName())) {
            LegendControlFactory.ServerProvider.addChanceLegend(LegendSourceName.PIXELMON, config.getStepSpawnChance(), true);
            event.setCanceled(true);
            return;
        }

        TaskTimer.builder()
                .delay(1L)
                .consume(task -> {
                    if (!pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                        task.cancel();
                        return;
                    }

                    if (config.isNotifyPersonalLegendarySpawn() && !player.hasDisconnected()) {
                        player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getSpawnPlayerLegendary()
                                .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                                .replace("%x%", String.valueOf((int) pixelmonEntity.getX()))
                                .replace("%y%", String.valueOf((int) pixelmonEntity.getY()))
                                .replace("%z%", String.valueOf((int) pixelmonEntity.getZ()))));
                    }

                    update(pixelmonEntity);
                    WebhookUtils.spawnWebhook(pixelmonEntity.getPokemon(), event.action.spawnLocation.biome);
                }).build();
    }

    private void update(PixelmonEntity pixelmonEntity) {
        ServerStorage storage = LegendControl.getInstance().getServerProvider().getServerStorage();

        storage.setChanceLegend(LegendSourceName.PIXELMON, LegendControl.getInstance().getConfig().getBaseChance(), false);
        storage.setLastLegend(pixelmonEntity.getPokemonName(), false);

        LegendControl.getInstance().getServerProvider().updateServerStorage(storage);

        LEGENDS.add(pixelmonEntity);
        setTimers(pixelmonEntity);
    }

    private void setTimers(PixelmonEntity pixelmonEntity) {
        ServerConfig config = LegendControl.getInstance().getConfig();

        if (config.getLocationTime() > 0) {
            TaskTimer.builder()
                    .delay(config.getLocationTime() * 20L)
                    .consume(task -> {
                        if (!LEGENDS.contains(pixelmonEntity) || !pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                            task.cancel();
                            return;
                        }

                        LegendControlEvent.Location event = new LegendControlEvent.Location(pixelmonEntity, pixelmonEntity.getX(), pixelmonEntity.getY(), pixelmonEntity.getZ());

                        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                            task.cancel();
                            return;
                        }

                        UtilChat.broadcast(LegendControl.getInstance().getLocale().getLocation()
                                .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                                .replace("%x%", String.valueOf((int) event.getX()))
                                .replace("%y%", String.valueOf((int) event.getY()))
                                .replace("%z%", String.valueOf((int) event.getZ())));

                        WebhookUtils.locationWebhook(pixelmonEntity);
                    }).build();
        }

        if (config.getDespawnTime() > 0) {
            TaskTimer.builder()
                    .delay(config.getDespawnTime() * 20L)
                    .consume(task -> {
                        if (!LEGENDS.contains(pixelmonEntity) || !pixelmonEntity.isAlive() || pixelmonEntity.hasOwner() || NeoForge.EVENT_BUS.post(new LegendControlEvent.ForceDespawn(pixelmonEntity)).isCanceled()) {
                            task.cancel();
                            return;
                        }

                        if (pixelmonEntity.battleController != null) {
                            pixelmonEntity.battleController.endBattle();
                        }

                        pixelmonEntity.remove(Entity.RemovalReason.KILLED);
                    }).build();
        }
    }
}
