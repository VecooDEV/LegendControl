package com.vecoo.legendcontrol_defender.listener;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.KeyEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
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
import com.vecoo.legendcontrol_defender.util.WebhookUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DefenderListener {
    private final Map<UUID, UUID> LEGENDARY_DEFENDER = new HashMap<>();

    public boolean hasLegendaryDefender(UUID pokemonUUID) {
        return LEGENDARY_DEFENDER.containsKey(pokemonUUID);
    }

    public boolean addLegendaryDefender(UUID pokemonUUID, UUID playerUUID) {
        if (hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        LEGENDARY_DEFENDER.put(pokemonUUID, playerUUID);
        return true;
    }

    public boolean removeLegendaryDefender(UUID pokemonUUID) {
        if (!hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        LEGENDARY_DEFENDER.remove(pokemonUUID);
        return true;
    }

    public boolean hasLegendaryPlayerOwner(UUID pokemonUUID, ServerPlayer player) {
        if (!hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        UUID ownerUUID = LEGENDARY_DEFENDER.get(pokemonUUID);

        if (ownerUUID.equals(player.getUUID())) {
            return false;
        }

        return !LegendControlFactory.PlayerProvider.hasPlayerTrust(ownerUUID, player.getUUID());
    }

    @SubscribeEvent
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

                    if (LegendControlDefender.getInstance().getConfig().getProtectedTime() > 0) {
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

                    NeoForge.EVENT_BUS.post(new LegendControlDefenderEvent.ExpiredDefender(pixelmonEntity));

                    removeLegendaryDefender(pixelmonEntity.getUUID());
                    WebhookUtils.defenderExpiredWebhook(pixelmonEntity);
                }).build();
    }

    @SubscribeEvent
    public void onKey(KeyEvent event) {
        if (event.key != EnumKeyPacketMode.ActionKeyEntity) {
            return;
        }

        PlayerPartyStorage party = StorageProxy.getStorageManager().getPartyNow(event.player);

        if (party == null) {
            return;
        }

        ServerPlayer player = event.player;

        for (Pokemon pokemon : party.getTeam()) {
            pokemon.ifEntityExists(pixelmonEntity -> {
                Entity target = pixelmonEntity.getTarget();

                if (target instanceof PixelmonEntity && hasLegendaryPlayerOwner(target.getUUID(), player) && !NeoForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender(pixelmonEntity, player)).isCanceled()) {
                    player.sendSystemMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getIncorrectCause()));
                    event.setCanceled(true);
                }
            });
        }
    }

    @SubscribeEvent
    public void onBattleStarted(BattleStartedEvent.Pre event) {
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

        if (participants.size() != 2 || player == null || wildPixelmon == null || player.getPlayer() == null || wildPixelmon.getEntity() == null || !hasLegendaryPlayerOwner(wildPixelmon.getEntity().getUUID(), player.getPlayer())) {
            return;
        }

        if (NeoForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender((PixelmonEntity) wildPixelmon.getEntity(), player.getPlayer())).isCanceled()) {
            return;
        }

        player.getPlayer().sendSystemMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getIncorrectCause()));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onStartCapture(CaptureEvent.StartCapture event) {
        ServerPlayer player = event.getPlayer();

        if (hasLegendaryPlayerOwner(event.getPokemon().getUUID(), player)) {
            if (NeoForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender(event.getPokemon().getPixelmonEntity().orElse(null), player)).isCanceled()) {
                return;
            }

            if (!player.isCreative()) {
                player.getInventory().add(event.getPokeBall().getBallItem());
            }

            player.sendSystemMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getIncorrectCause()));
            event.setCanceled(true);
        }
    }
}
