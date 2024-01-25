package com.vecoo.legendcontrol.listener;

import com.envyful.api.forge.chat.UtilChatColour;
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
import com.vecoo.legendcontrol.providers.PlayerTrust;
import com.vecoo.legendcontrol.util.Task;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LegendarySpawnListener {

    public static HashMap<PixelmonEntity, UUID> legendMap = new HashMap<>();
    public static int legendaryChance;

    private boolean checkLegendMap(ServerPlayer player, PixelmonEntity pixelmon) {
        if (legendMap.containsKey(pixelmon) && !player.isCreative()) {
            UUID invokerUUID = legendMap.get(pixelmon);
            PlayerTrust invokerTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(invokerUUID);
            List<UUID> allowedPlayers = new ArrayList<>();

            if (LegendControl.getInstance().getTrustProvider().getKey(invokerUUID)) {
                allowedPlayers.addAll(invokerTrust.getPlayerList());
            }

            if (!invokerUUID.equals(player.getUUID()) && !allowedPlayers.contains(player.getUUID())) {
                player.sendSystemMessage(UtilChatColour.colour(
                        LegendControl.getInstance().getLocale().getMessages().getIncorrectCause()));
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onLegendarySpawnControl(LegendarySpawnEvent.DoSpawn event) {
        if (LegendControl.getInstance().getConfig().isNewLegendarySpawn()) {
            PixelmonEntity spawnEntity = event.action.getOrCreateEntity();
            ServerPlayer player = (ServerPlayer) event.action.spawnLocation.cause;

            int random = ThreadLocalRandom.current().nextInt(100);
            int num = LegendControl.getInstance().getConfig().getLegendProtectedTime();

            if (random > legendaryChance) {
                legendaryChance += LegendControl.getInstance().getConfig().getStepSpawnChance();
                event.setCanceled(true);
            } else {
                player.sendSystemMessage(UtilChatColour.colour(
                        LegendControl.getInstance().getLocale().getMessages().getSpawnPlayerLegendary()));
                legendMap.put(spawnEntity, player.getUUID());
                legendaryChance = LegendControl.getInstance().getConfig().getBaseChance();

                if (num > 0 && LegendControl.getInstance().getConfig().isLegendaryDefender()) {
                    Task.builder()
                            .execute(() -> legendMap.remove(spawnEntity))
                            .delay(20L * num)
                            .interval(20L * num)
                            .build();
                }
            }
        }
    }

    @SubscribeEvent
    public void onLegendarySpawnNotify(LegendarySpawnEvent.DoSpawn event) {
        if (LegendControl.getInstance().getConfig().isNotifyLegendarySpawn() && !LegendControl.getInstance().getConfig().isNewLegendarySpawn()) {
            ServerPlayer player = (ServerPlayer) event.action.spawnLocation.cause;
            player.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getSpawnPlayerLegendary()));
        }
    }

    @SubscribeEvent
    public void onBattleKey(KeyEvent event) {
        if (event.key == EnumKeyPacketMode.ActionKeyEntity && !legendMap.isEmpty()) {
            ServerPlayer player = event.player;

            for (Pokemon pokemon : StorageProxy.getStorageManager().getParty(event.player).join()) {
                pokemon.ifEntityExists(entityPixelmon -> {
                    final Entity target = entityPixelmon.getTarget();
                    if (target instanceof PixelmonEntity) {
                        final PixelmonEntity targetPoke = (PixelmonEntity) target;
                        if (checkLegendMap(player, targetPoke)) {
                            event.setCanceled(true);
                        }
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onStartBattle(BattleStartedEvent event) {
        BattleParticipant participant1 = event.getTeamOne()[0];
        BattleParticipant participant2 = event.getTeamTwo()[0];
        if (participant1 instanceof WildPixelmonParticipant || participant2 instanceof WildPixelmonParticipant) {
            if (participant1 instanceof WildPixelmonParticipant
                    && participant2 instanceof WildPixelmonParticipant) {
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

            ServerPlayer player = (ServerPlayer) playerParticipant.getEntity();
            PixelmonEntity pixelmon = (PixelmonEntity) wildPokemon.getEntity();

            if (checkLegendMap(player, pixelmon)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.StartCapture event) {
        ServerPlayer player = event.getPlayer();
        PixelmonEntity pixelmon = event.getPokemon();

        if (checkLegendMap(player, pixelmon)) {
            event.setCanceled(true);
            ItemStack pokeball = event.getPokeBall().getBallType().getBallItem();
            player.getInventory().add(pokeball);
        }
    }

    @SubscribeEvent
    public void onCheckSpawns(LegendaryCheckSpawnsEvent event) {
        event.shouldShowChance = false;
    }
}
