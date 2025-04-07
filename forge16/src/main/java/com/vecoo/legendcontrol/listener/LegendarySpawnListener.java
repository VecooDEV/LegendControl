package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.util.Utils;
import com.vecoo.legendcontrol.util.WebhookUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LegendarySpawnListener {
    public static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    public static final HashSet<PixelmonEntity> legendaryList = new HashSet<>();

    public static HashSet<PixelmonEntity> getLegendaryList() {
        return legendaryList;
    }

    @SubscribeEvent
    public void onChoosePlayer(LegendarySpawnEvent.ChoosePlayer event) {
        ServerConfig config = LegendControl.getInstance().getConfig();

        if (config.isBlacklistDimensions() && config.getBlacklistDimensionList().contains(event.player.getLevel().dimension().location().getPath())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        ServerConfig config = LegendControl.getInstance().getConfig();
        PixelmonEntity pixelmonEntity = event.action.getOrCreateEntity();
        String pokemonName = pixelmonEntity.getPokemonName();

        if (config.isBlacklistLegendary() && config.getBlockedLegendary().contains(pokemonName)) {
            event.setCanceled(true);
            Utils.doSpawn();
            return;
        }

        if (!config.isLegendaryRepeat() && LegendControlFactory.ServerProvider.getLastLegend().equals(pokemonName)) {
            event.setCanceled(true);
            Utils.doSpawn();
            return;
        }

        if (config.isNotifyPersonalLegendarySpawn()) {
            event.action.spawnLocation.cause.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getSpawnPlayerLegendary()), Util.NIL_UUID);
        }

        updateServerData(pixelmonEntity);
        WebhookUtils.spawnWebhook(pixelmonEntity);

        if (config.getLocationTime() > 0) {
            setLocationTimer(pixelmonEntity);
        }
    }

    private void updateServerData(PixelmonEntity pixelmonEntity) {
        LegendControlFactory.ServerProvider.setLegendaryChance(LegendControl.getInstance().getConfig().getBaseChance());
        LegendControlFactory.ServerProvider.setLastLegend(pixelmonEntity.getPokemonName());
        LegendControlFactory.ServerProvider.addLegends(pixelmonEntity.getUUID());
        legendaryList.add(pixelmonEntity);
        Utils.countSpawn = 0;
    }

    private void setLocationTimer(PixelmonEntity pixelmonEntity) {
        SCHEDULER.schedule(() -> {
            if (legendaryList.contains(pixelmonEntity) && pixelmonEntity.isAlive() && !pixelmonEntity.hasOwner()) {
                MinecraftServer server = LegendControl.getInstance().getServer();

                server.execute(() -> UtilChat.broadcast(LegendControl.getInstance().getLocale().getLocation()
                        .replace("%pokemon%", pixelmonEntity.getSpecies().getName())
                        .replace("%x%", String.valueOf((int) pixelmonEntity.getX()))
                        .replace("%y%", String.valueOf((int) pixelmonEntity.getY()))
                        .replace("%z%", String.valueOf((int) pixelmonEntity.getZ())), LegendControl.getInstance().getServer()));

                WebhookUtils.locationWebhook(pixelmonEntity);
            }
        }, LegendControl.getInstance().getConfig().getLocationTime(), TimeUnit.SECONDS);
    }
}
