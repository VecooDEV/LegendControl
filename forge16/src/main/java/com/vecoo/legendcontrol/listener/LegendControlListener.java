package com.vecoo.legendcontrol.listener;

import com.envyful.api.forge.chat.UtilChatColour;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
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
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.util.Task;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LegendControlListener {

    public static HashMap<PixelmonEntity, UUID> legendMap = new HashMap<>();

    private boolean hasMap(ServerPlayerEntity player, PixelmonEntity pokemon) {
        if (legendMap.containsKey(pokemon) && !player.abilities.instabuild) {
            UUID invoker = legendMap.get(pokemon);

            if (!invoker.equals(player.getUUID()) && !LegendControl.getInstance().getTrustProvider().getPlayerTrust(invoker).getPlayerList().contains(player.getUUID())) {
                player.sendMessage(UtilChatColour.colour(
                        LegendControl.getInstance().getLocale().getMessages().getIncorrectCause()), Util.NIL_UUID);
                return false;
            }
        }
        return true;
    }

    private static boolean isBlackListed(Pokemon pokemon) {
        for (PokemonSpecification blocked : LegendControl.getInstance().getConfig().getBlockedLegendary()) {
            if (blocked != null && blocked.matches(pokemon)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onLegendarySpawnControl(LegendarySpawnEvent.DoSpawn event) {
        ServerConfig config = LegendControl.getInstance().getConfig();
        PixelmonEntity pokemon = event.action.getOrCreateEntity();
        ServerPlayerEntity player = (ServerPlayerEntity) event.action.spawnLocation.cause;

        if (ThreadLocalRandom.current().nextInt(100) > LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance()
                && config.isNewLegendarySpawn()) {
            LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().addChance(config.getStepSpawnChance());
            event.setCanceled(true);
            return;
        }

        if (isBlackListed(event.action.getOrCreateEntity().getPokemon())) {
            LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().addChance(config.getStepSpawnChance());
            event.setCanceled(true);
            return;
        }

        if (LegendControl.getInstance().getConfig().isNotifyLegendarySpawn()) {
            player.sendMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getSpawnPlayerLegendary()), Util.NIL_UUID);
        }

        legendMap.put(pokemon, player.getUUID());

        LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().setChance(config.getBaseChance());

        int num = config.getLegendProtectedTime();

        if (num > 0 && config.isLegendaryDefender()) {
            Task.builder()
                    .execute(() -> {
                        LegendControl.getInstance().getServer().getPlayerList().broadcastMessage(UtilChatColour.colour(
                                LegendControl.getInstance().getLocale().getMessages().getProtection()
                                        .replace("%pokemon%", pokemon.getSpecies().getName())), ChatType.CHAT, Util.NIL_UUID);
                        legendMap.remove(pokemon);
                    })
                    .delay(20L * num)
                    .interval(20L * num)
                    .build();
        }
    }

    @SubscribeEvent
    public void onBattleKey(KeyEvent event) {
        if (event.key == EnumKeyPacketMode.ActionKeyEntity && !legendMap.isEmpty() && LegendControl.getInstance().getConfig().isLegendaryDefender()) {
            for (Pokemon pokemon : StorageProxy.getStorageManager().getParty(event.player).getTeam()) {
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
    public void onStartBattle(BattleStartedEvent event) {
        if (LegendControl.getInstance().getConfig().isLegendaryDefender()) {
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

                if (!hasMap((ServerPlayerEntity) playerParticipant.getEntity(), (PixelmonEntity) wildPokemon.getEntity())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.StartCapture event) {
        if (LegendControl.getInstance().getConfig().isLegendaryDefender()) {
            ServerPlayerEntity player = event.getPlayer();

            if (!hasMap(player, event.getPokemon())) {
                ItemStack pokeball = event.getPokeBall().getBallType().getBallItem();
                player.inventory.add(pokeball);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onCheckSpawns(LegendaryCheckSpawnsEvent event) {
        if (LegendControl.getInstance().getConfig().isNewLegendarySpawn()) {
            event.shouldShowChance = false;
        }
    }
}
