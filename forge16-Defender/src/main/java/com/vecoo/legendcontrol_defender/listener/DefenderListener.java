package com.vecoo.legendcontrol_defender.listener;

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
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol_defender.utils.TaskUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefenderListener {
    private final Map<UUID, UUID> legendaryDefender = new ConcurrentHashMap<>();

    public boolean hasLegendaryDefender(UUID pokemonUUID) {
        return legendaryDefender.containsKey(pokemonUUID);
    }

    public boolean addLegendaryDefender(UUID pokemonUUID, UUID playerUUID) {
        if (hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        legendaryDefender.put(pokemonUUID, playerUUID);
        return true;
    }

    public boolean removeLegendaryDefender(UUID pokemonUUID) {
        if (!hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        legendaryDefender.remove(pokemonUUID);
        return true;
    }

    public boolean hasLegendaryPlayerOwner(UUID pokemonUUID, ServerPlayerEntity player) {
        if (!hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        UUID ownerUUID = legendaryDefender.get(pokemonUUID);

        if (ownerUUID.equals(player.getUUID())) {
            return false;
        }

        return !LegendControlFactory.PlayerProvider.hasPlayerTrust(ownerUUID, player.getUUID());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        PixelmonEntity pixelmonEntity = event.action.getOrCreateEntity();

        addLegendaryDefender(pixelmonEntity.getUUID(), event.action.spawnLocation.cause.getUUID());

        TaskUtils.builder()
                .delay(20L)
                .consume(task -> {
                    if (!pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                        task.cancel();
                        return;
                    }

                    startDefender(pixelmonEntity);
                }).build();
    }

    private void startDefender(PixelmonEntity pixelmonEntity) {
        TaskUtils.builder()
                .delay(LegendControlDefender.getInstance().getConfig().getProtectedTime() * 20L)
                .consume(task -> {
                    if (hasLegendaryDefender(pixelmonEntity.getUUID()) && pixelmonEntity.isAlive() && !pixelmonEntity.hasOwner()) {
                        UtilChat.broadcast(LegendControlDefender.getInstance().getLocale().getProtection()
                                .replace("%pokemon%", pixelmonEntity.getPokemonName()), LegendControlDefender.getInstance().getServer());
                    }
                    removeLegendaryDefender(pixelmonEntity.getUUID());
                }).build();
    }

    @SubscribeEvent
    public void onKey(KeyEvent event) {
        if (event.key != EnumKeyPacketMode.ActionKeyEntity) {
            return;
        }

        for (Pokemon pokemon : StorageProxy.getStorageManager().getParty(event.player).getTeam()) {
            pokemon.ifEntityExists(entityPixelmon -> {
                Entity target = entityPixelmon.getTarget();
                if (target instanceof PixelmonEntity) {
                    if (hasLegendaryPlayerOwner(target.getUUID(), event.player)) {
                        event.setCanceled(true);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onBattleStarted(BattleStartedEvent event) {
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

            if (hasLegendaryPlayerOwner(wildPokemon.getEntity().getUUID(), (ServerPlayerEntity) playerParticipant.getEntity())) {
                playerParticipant.getEntity().sendMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getIncorrectCause()), Util.NIL_UUID);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onStartCapture(CaptureEvent.StartCapture event) {
        ServerPlayerEntity player = event.getPlayer();

        if (hasLegendaryPlayerOwner(event.getPokemon().getUUID(), player)) {
            if (!player.isCreative()) {
                player.inventory.add(event.getPokeBall().getBallType().getBallItem());
            }
            event.setCanceled(true);
        }
    }
}
