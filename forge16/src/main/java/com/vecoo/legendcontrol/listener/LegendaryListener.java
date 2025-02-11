package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.KeyEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendaryCheckSpawnsEvent;
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
import com.vecoo.legendcontrol.storage.LegendFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class LegendaryListener {
    public static HashMap<PixelmonEntity, UUID> playerEntity = new HashMap<>();

    private boolean hasPlayerEntity(ServerPlayerEntity player, PixelmonEntity pokemon) {
        if (playerEntity.containsKey(pokemon)) {
            UUID invokerUUID = playerEntity.get(pokemon);

            if (!invokerUUID.equals(player.getUUID()) && !LegendFactory.hasPlayerTrust(invokerUUID, player.getUUID())) {
                player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getIncorrectCause()), Util.NIL_UUID);
                return false;
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onChoosePlayer(LegendarySpawnEvent.ChoosePlayer event) {
        ServerConfig config = LegendControl.getInstance().getConfig();
        ServerPlayerEntity player = event.player;

        if (config.getBlacklistDimensionList().contains(player.getLevel().dimension().location().getPath()) && config.isBlacklistDimensions()) {
            event.setCanceled(true);
            return;
        }

        LegendFactory.updatePlayerIP(player);

        if (LegendFactory.getPlayersIP().containsValue(player.getIpAddress()) && config.getMaxPlayersIP() > 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        ServerConfig config = LegendControl.getInstance().getConfig();
        ServerPlayerEntity player = (ServerPlayerEntity) event.action.spawnLocation.cause;
        PixelmonEntity pokemon = event.action.getOrCreateEntity();

        if (LegendControl.getInstance().getConfig().getBlockedLegendary().contains(pokemon.getPokemonName()) && config.isBlacklistLegendary()) {
            Utils.doSpawn();
            event.setCanceled(true);
            return;
        }

        if (!config.isLegendaryRepeat() && LegendFactory.getLastLegend().equals(pokemon.getPokemonName())) {
            Utils.doSpawn();
            event.setCanceled(true);
            return;
        }

        if (config.isNotifyLegendarySpawn()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getSpawnPlayerLegendary()), Util.NIL_UUID);
        }

        playerEntity.put(pokemon, player.getUUID());

        LegendFactory.setLegendaryChance(config.getBaseChance());
        LegendFactory.setLastLegend(pokemon.getPokemonName());
        LegendFactory.replacePlayerIP(player.getUUID(), player.getIpAddress());

        Utils.countSpawn = 0;

        int protectedTime = config.getProtectedTime();
        int locationTime = config.getLocationTime();

        Timer timer = new Timer();

        if (protectedTime > 0) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (pokemon.isAlive() && playerEntity.containsKey(pokemon)) {
                        UtilChat.broadcast(LegendControl.getInstance().getLocale().getMessages().getProtection()
                                .replace("%pokemon%", pokemon.getSpecies().getName()), LegendControl.getInstance().getServer());
                    }
                    playerEntity.remove(pokemon);
                }
            }, protectedTime * 1000L);
        }

        if (locationTime > 0) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (pokemon.isAlive() && !pokemon.hasOwner()) {
                        UtilChat.broadcast(LegendControl.getInstance().getLocale().getMessages().getLocation()
                                .replace("%pokemon%", pokemon.getSpecies().getName())
                                .replace("%x%", String.valueOf((int) pokemon.getX()))
                                .replace("%y%", String.valueOf((int) pokemon.getY()))
                                .replace("%z%", String.valueOf((int) pokemon.getZ())), LegendControl.getInstance().getServer());
                    }
                }
            }, locationTime * 1000L);
        }
    }

    @SubscribeEvent
    public void onKey(KeyEvent event) {
        if (event.key == EnumKeyPacketMode.ActionKeyEntity && !playerEntity.isEmpty() && LegendControl.getInstance().getConfig().getProtectedTime() != 0) {
            for (Pokemon pokemon : StorageProxy.getStorageManager().getParty(event.player).getTeam()) {
                pokemon.ifEntityExists(entityPixelmon -> {
                    Entity target = entityPixelmon.getTarget();
                    if (target instanceof PixelmonEntity) {
                        if (!hasPlayerEntity(event.player, (PixelmonEntity) target)) {
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

                if (!hasPlayerEntity((ServerPlayerEntity) playerParticipant.getEntity(), (PixelmonEntity) wildPokemon.getEntity())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.StartCapture event) {
        if (LegendControl.getInstance().getConfig().getProtectedTime() > 0) {
            ServerPlayerEntity player = event.getPlayer();

            if (!hasPlayerEntity(player, event.getPokemon())) {
                player.inventory.add(event.getPokeBall().getBallType().getBallItem());
                playerEntity.remove(event.getPokemon());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLegendaryCheckSpawns(LegendaryCheckSpawnsEvent event) {
        event.shouldShowTime = false;
        event.shouldShowChance = false;
    }
}