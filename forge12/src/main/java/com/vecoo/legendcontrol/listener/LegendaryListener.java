package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.KeyEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.comm.packetHandlers.EnumKeyPacketMode;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.storage.player.PlayerFactory;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import com.vecoo.legendcontrol.task.ParticleTask;
import com.vecoo.legendcontrol.util.UtilLegendarySpawn;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class LegendaryListener {
    public static HashMap<EntityPixelmon, UUID> legendMap = new HashMap<>();

    private boolean hasMap(EntityPlayerMP player, EntityPixelmon pokemon) {
        if (legendMap.containsKey(pokemon)) {
            UUID invokerUUID = legendMap.get(pokemon);

            if (!invokerUUID.equals(player.getUniqueID()) && !PlayerFactory.hasPlayerTrust(invokerUUID, player.getUniqueID())) {
                player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getIncorrectCause()));
                return false;
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onLegendarySpawnControl(LegendarySpawnEvent.DoSpawn event) {
        ServerConfig config = LegendControl.getInstance().getConfig();
        EntityPlayerMP player = (EntityPlayerMP) event.action.spawnLocation.cause;
        EntityPixelmon pokemon = event.action.getOrCreateEntity();

        if (LegendControl.getInstance().getConfig().getBlockedLegendary().contains(pokemon.getPokemonName()) && config.isBlacklistLegendary()) {
            UtilLegendarySpawn.spawn();
            event.setCanceled(true);
            return;
        }

        if (!config.isLegendaryRepeat() && ServerFactory.getLastLegend().equals(pokemon.getPokemonName())) {
            UtilLegendarySpawn.spawn();
            event.setCanceled(true);
            return;
        }

        if (config.isNotifyLegendarySpawn()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getSpawnPlayerLegendary()));
        }

        legendMap.put(pokemon, player.getUniqueID());

        ParticleTask.addPokemon(pokemon);

        ServerFactory.setLegendaryChance(config.getBaseChance());
        ServerFactory.setLastLegend(pokemon.getPokemonName());
        ServerFactory.replacePlayerIP(player.getUniqueID(), player.getPlayerIP());

        UtilLegendarySpawn.countSpawn = 0;

        int num = config.getProtectedTime();

        if (num > 0) {
            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (pokemon.isEntityAlive() && legendMap.containsKey(pokemon)) {
                        UtilChat.broadcast(LegendControl.getInstance().getLocale().getProtection()
                                .replace("%pokemon%", pokemon.getSpecies().getLocalizedName()));
                    }
                    legendMap.remove(pokemon);
                }
            }, num * 1000L);
        }
    }

    @SubscribeEvent
    public void onBattleKey(KeyEvent event) {
        if (event.key == EnumKeyPacketMode.ActionKeyEntity && !legendMap.isEmpty() && LegendControl.getInstance().getConfig().getProtectedTime() != 0) {
            for (Pokemon pokemon : Pixelmon.storageManager.getParty(event.player).getTeam()) {
                pokemon.ifEntityExists(entityPixelmon -> {
                    Entity target = entityPixelmon.getAttackTarget();
                    if (target instanceof EntityPixelmon) {
                        if (!hasMap(event.player, (EntityPixelmon) target)) {
                            event.setCanceled(true);
                        }
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onStartBattle(BattleStartedEvent event) {
        if (LegendControl.getInstance().getConfig().getProtectedTime() > 0) {
            BattleParticipant participant1 = event.participant1[0];
            BattleParticipant participant2 = event.participant2[0];
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

                if (!hasMap((EntityPlayerMP) playerParticipant.getEntity(), (EntityPixelmon) wildPokemon.getEntity())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.StartCapture event) {
        if (LegendControl.getInstance().getConfig().getProtectedTime() > 0) {
            EntityPlayerMP player = event.player;

            if (!hasMap(player, event.getPokemon())) {
                legendMap.remove(event.getPokemon());
                player.inventory.addItemStackToInventory(new ItemStack(event.pokeball.getType().getItem(), 1));
                event.setCanceled(true);
            }
        }
    }
}