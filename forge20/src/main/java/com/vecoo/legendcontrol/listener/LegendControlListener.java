package com.vecoo.legendcontrol.listener;

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
import com.vecoo.extraapi.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.util.Task;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LegendControlListener {

    public static HashMap<PixelmonEntity, UUID> legendMap = new HashMap<>();

    private boolean hasMap(ServerPlayer player, PixelmonEntity pokemon) {
        if (legendMap.containsKey(pokemon) && !player.getAbilities().instabuild) {
            UUID invoker = legendMap.get(pokemon);

            if (!invoker.equals(player.getUUID()) && !LegendControl.getInstance().getTrustProvider().getPlayerTrust(invoker).getPlayerList().contains(player.getUUID())) {
                player.sendSystemMessage(UtilChat.formatMessage(
                        LegendControl.getInstance().getLocale().getMessages().getIncorrectCause()));
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
        ServerPlayer player = (ServerPlayer) event.action.spawnLocation.cause;

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
            player.sendSystemMessage(UtilChat.formatMessage(
                    LegendControl.getInstance().getLocale().getMessages().getSpawnPlayerLegendary()));
        }

        legendMap.put(pokemon, player.getUUID());

        LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().setChance(config.getBaseChance());

        int num = config.getLegendProtectedTime();

        if (num > 0 && config.isLegendaryDefender()) {
            Task.builder()
                    .execute(() -> {
                        UtilChat.broadcast(LegendControl.getInstance().getLocale().getMessages().getProtection()
                                .replace("%pokemon%", pokemon.getSpecies().getName()), LegendControl.getInstance().getServer());
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

                if (!hasMap((ServerPlayer) playerParticipant.getEntity(), (PixelmonEntity) wildPokemon.getEntity())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.StartCapture event) {
        if (LegendControl.getInstance().getConfig().isLegendaryDefender()) {
            ServerPlayer player = event.getPlayer();

            if (!hasMap(player, event.getPokemon())) {
                ItemStack pokeball = event.getPokeBall().getBallType().getBallItem();
                player.getInventory().add(pokeball);
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