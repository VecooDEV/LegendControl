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
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.LegendControlDefenderEvent;
import com.vecoo.legendcontrol_defender.api.factory.LegendControlFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DefenderListener {
    private final Map<UUID, UUID> legendaryDefender = new HashMap<>();

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

        TaskTimer.builder()
                .delay(1L)
                .consume(task -> {
                    if (!pixelmonEntity.isAlive() || pixelmonEntity.hasOwner()) {
                        task.cancel();
                        return;
                    }

                    if (LegendControlDefender.getInstance().getConfig().getProtectedTime() != 0) {
                        startDefender(pixelmonEntity);
                    }
                }).build();
    }

    private void startDefender(PixelmonEntity pixelmonEntity) {
        TaskTimer.builder()
                .delay(LegendControlDefender.getInstance().getConfig().getProtectedTime() * 20L)
                .consume(task -> {
                    if (hasLegendaryDefender(pixelmonEntity.getUUID()) && pixelmonEntity.isAlive() && !pixelmonEntity.hasOwner()) {
                        UtilChat.broadcast(LegendControlDefender.getInstance().getLocale().getProtection()
                                .replace("%pokemon%", pixelmonEntity.getPokemonName()));
                    }

                    removeLegendaryDefender(pixelmonEntity.getUUID());

                    MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.ExpiredDefender(pixelmonEntity));
                }).build();
    }

    @SubscribeEvent
    public void onKey(KeyEvent event) {
        if (event.key != EnumKeyPacketMode.ActionKeyEntity) {
            return;
        }

        ServerPlayerEntity player = event.player;

        for (Pokemon pokemon : StorageProxy.getStorageManager().getParty(player).getTeam()) {
            pokemon.ifEntityExists(pixelmonEntity -> {
                Entity target = pixelmonEntity.getTarget();

                if (!(target instanceof PixelmonEntity)) {
                    return;
                }

                if (!hasLegendaryPlayerOwner(target.getUUID(), player)) {
                    return;
                }

                if (MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender(pixelmonEntity, player))) {
                    return;
                }

                player.getEntity().sendMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getIncorrectCause()), Util.NIL_UUID);
                event.setCanceled(true);
            });
        }
    }

    @SubscribeEvent
    public void onBattleStarted(BattleStartedEvent event) {
        if (event.getBattleController().isPvP()) {
            return;
        }

        List<BattleParticipant> participants = event.getBattleController().participants;

        PlayerParticipant player = participants.stream()
                .filter(PlayerParticipant.class::isInstance)
                .map(PlayerParticipant.class::cast)
                .findFirst()
                .orElse(null);

        WildPixelmonParticipant wildPixelmon = participants.stream()
                .filter(WildPixelmonParticipant.class::isInstance)
                .map(WildPixelmonParticipant.class::cast)
                .findFirst()
                .orElse(null);

        if (participants.size() != 2 || player == null || wildPixelmon == null) {
            return;
        }

        if (!hasLegendaryPlayerOwner(wildPixelmon.getEntity().getUUID(), (ServerPlayerEntity) player.getEntity())) {
            return;
        }

        if (MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender((PixelmonEntity) wildPixelmon.getEntity(), (ServerPlayerEntity) player.getEntity()))) {
            return;
        }

        player.getEntity().sendMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getIncorrectCause()), Util.NIL_UUID);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onStartCapture(CaptureEvent.StartCapture event) {
        ServerPlayerEntity player = event.getPlayer();

        if (hasLegendaryPlayerOwner(event.getPokemon().getUUID(), player)) {
            if (MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender(event.getPokemon(), player))) {
                return;
            }

            if (!player.isCreative()) {
                player.inventory.add(event.getPokeBall().getBallType().getBallItem());
            }

            player.sendMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getIncorrectCause()), Util.NIL_UUID);
            event.setCanceled(true);
        }
    }
}
