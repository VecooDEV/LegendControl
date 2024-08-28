package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.KeyEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.comm.packetHandlers.EnumKeyPacketMode;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.storage.player.LegendPlayerFactory;
import com.vecoo.legendcontrol.storage.server.LegendServerFactory;
import com.vecoo.legendcontrol.task.ParticleTask;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class LegendaryListener {
    public static HashMap<PixelmonEntity, UUID> legendMap = new HashMap<>();

    private boolean hasMap(ServerPlayer player, PixelmonEntity pokemon) {
        if (legendMap.containsKey(pokemon)) {
            UUID invokerUUID = legendMap.get(pokemon);

            if (!invokerUUID.equals(player.getUUID()) && !LegendPlayerFactory.hasPlayerTrust(invokerUUID, player.getUUID())) {
                player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getIncorrectCause()));
                return false;
            }
        }
        return true;
    }

    public void onChoosePlayer(LegendarySpawnEvent.ChoosePlayer event) {
        ServerConfig config = LegendControl.getInstance().getConfig();
        ServerPlayer player = event.player;

        if (LegendServerFactory.getPlayersBlacklist().contains(player.getUUID())) {
            event.setCanceled(true);
            return;
        }

        LegendServerFactory.updatePlayerIP(player);

        if (LegendServerFactory.getPlayersIP().containsValue(player.getIpAddress()) && config.getMaxPlayersIP() > 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        ServerConfig config = LegendControl.getInstance().getConfig();
        ServerPlayer player = (ServerPlayer) event.action.spawnLocation.cause;
        PixelmonEntity pokemon = event.action.getOrCreateEntity();

        if (LegendControl.getInstance().getConfig().getBlockedLegendary().contains(pokemon.getPokemonName()) && config.isBlacklistLegendary()) {
            Utils.doSpawn();
            event.setCanceled(true);
            return;
        }

        if (!config.isLegendaryRepeat() && LegendServerFactory.getLastLegend().equals(pokemon.getPokemonName())) {
            Utils.doSpawn();
            event.setCanceled(true);
            return;
        }

        if (config.isNotifyLegendarySpawn()) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getSpawnPlayerLegendary()));
        }

        legendMap.put(pokemon, player.getUUID());

        ParticleTask.addPokemon(pokemon);

        LegendServerFactory.setLegendaryChance(config.getBaseChance());
        LegendServerFactory.setLastLegend(pokemon.getPokemonName());
        LegendServerFactory.replacePlayerIP(player.getUUID(), player.getIpAddress());

        Utils.countSpawn = 0;

        int num = config.getProtectedTime();

        if (num > 0) {
            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (pokemon.isAlive() && legendMap.containsKey(pokemon)) {
                        UtilChat.broadcast(LegendControl.getInstance().getLocale().getMessages().getProtection()
                                .replace("%pokemon%", pokemon.getSpecies().getName()));
                    }
                    legendMap.remove(pokemon);
                }
            }, num * 1000L);
        }
    }

    @SubscribeEvent
    public void onKey(KeyEvent event) {
        if (event.key == EnumKeyPacketMode.ActionKeyEntity && !legendMap.isEmpty() && LegendControl.getInstance().getConfig().getProtectedTime() != 0) {
            for (Pokemon pokemon : StorageProxy.getStorageManager().getParty(event.player).join()) {
                pokemon.ifEntityExists(entityPixelmon -> {
                    Entity target = entityPixelmon.getTarget();
                    if (target instanceof PixelmonEntity) {
                        if (!hasMap(event.player, (PixelmonEntity) target)) {
                            event.setCanceled(true);
                        }
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onBattleStarted(BattleStartedEvent event) {
        if (LegendControl.getInstance().getConfig().getProtectedTime() > 0) {
            BattleParticipant participant1 = event.getTeamOne()[0];
            BattleParticipant participant2 = event.getTeamTwo()[0];
            if (participant1 instanceof WildPixelmonParticipant || participant2 instanceof WildPixelmonParticipant) {
                if (participant1 instanceof WildPixelmonParticipant && participant2 instanceof WildPixelmonParticipant) {
                    return;
                }

                PlayerParticipant playerParticipant;
                if (participant1 instanceof PlayerParticipant) {
                    playerParticipant = (PlayerParticipant) participant1;
                } else {
                    assert participant2 instanceof PlayerParticipant;
                    playerParticipant = (PlayerParticipant) participant2;
                }

                WildPixelmonParticipant wildPokemon = participant1 instanceof PlayerParticipant ? (WildPixelmonParticipant) participant2 : (WildPixelmonParticipant) participant1;

                if (!hasMap((ServerPlayer) playerParticipant.getEntity(), (PixelmonEntity) wildPokemon.getEntity())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.StartCapture event) {
        if (LegendControl.getInstance().getConfig().getProtectedTime() > 0) {
            ServerPlayer player = event.getPlayer();

            if (!hasMap(player, event.getPokemon())) {
                player.getInventory().add(event.getPokeBall().getBallType().getBallItem());
                legendMap.remove(event.getPokemon());
                event.setCanceled(true);
            }
        }
    }
}