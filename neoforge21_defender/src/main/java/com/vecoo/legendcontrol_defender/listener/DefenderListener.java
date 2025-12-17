package com.vecoo.legendcontrol_defender.listener;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.KeyEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.comm.packetHandlers.EnumKeyPacketMode;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.LegendControlDefenderEvent;
import com.vecoo.legendcontrol_defender.api.service.LegendControlService;
import com.vecoo.legendcontrol_defender.util.WebhookUtils;
import lombok.val;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefenderListener {
    private final Map<UUID, UUID> LEGENDARY_DEFENDER = new HashMap<>();

    public boolean hasLegendaryDefender(@NotNull UUID pokemonUUID) {
        return LEGENDARY_DEFENDER.containsKey(pokemonUUID);
    }

    public boolean addLegendaryDefender(@NotNull UUID pokemonUUID, @NotNull UUID playerUUID) {
        if (hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        LEGENDARY_DEFENDER.put(pokemonUUID, playerUUID);
        return true;
    }

    public boolean removeLegendaryDefender(@NotNull UUID pokemonUUID) {
        if (!hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        LEGENDARY_DEFENDER.remove(pokemonUUID);
        return true;
    }

    public boolean hasLegendaryPlayerOwner(@NotNull UUID pokemonUUID, @NotNull ServerPlayer player) {
        if (!hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        UUID ownerUUID = LEGENDARY_DEFENDER.get(pokemonUUID);

        if (ownerUUID.equals(player.getUUID())) {
            return false;
        }

        return !LegendControlService.hasPlayerTrust(ownerUUID, player.getUUID());
    }

    @SubscribeEvent
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        val pixelmonEntity = event.action.getOrCreateEntity();

        addLegendaryDefender(pixelmonEntity.getUUID(), event.action.spawnLocation.cause.getUUID());

        if (LegendControlDefender.getInstance().getServerConfig().getProtectedTime() > 0) {
            startDefender(pixelmonEntity);
        }
    }

    private void startDefender(@NotNull PixelmonEntity pixelmonEntity) {
        TaskTimer.builder()
                .delay(LegendControlDefender.getInstance().getServerConfig().getProtectedTime() * 20L)
                .consume(task -> {
                    if (removeLegendaryDefender(pixelmonEntity.getUUID()) && pixelmonEntity.isAlive() && !pixelmonEntity.hasOwner()) {
                        val event = new LegendControlDefenderEvent.ExpiredDefender(pixelmonEntity);

                        if (!event.isCanceled()) {
                            UtilChat.broadcast(LegendControlDefender.getInstance().getLocaleConfig().getProtection()
                                    .replace("%pokemon%", pixelmonEntity.getPokemonName()));
                            WebhookUtils.defenderExpiredWebhook(pixelmonEntity);
                        }
                    }
                }).build();
    }

    @SubscribeEvent
    public void onKey(KeyEvent event) {
        if (event.key != EnumKeyPacketMode.ActionKeyEntity) {
            return;
        }

        val party = StorageProxy.getStorageManager().getPartyNow(event.player);

        if (party == null) {
            return;
        }

        val player = event.player;

        for (Pokemon pokemon : party.getTeam()) {
            pokemon.ifEntityExists(pixelmonEntity -> {
                val target = pixelmonEntity.getTarget();

                if (target instanceof PixelmonEntity && hasLegendaryPlayerOwner(target.getUUID(), player)
                    && !NeoForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender(pixelmonEntity, player)).isCanceled()) {
                    player.sendSystemMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocaleConfig().getIncorrectCause()));
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

        val participants = event.getBattleController().participants;

        val player = participants.stream()
                .filter(PlayerParticipant.class::isInstance)
                .map(PlayerParticipant.class::cast)
                .findFirst()
                .orElse(null);

        val wildPixelmon = participants.stream()
                .filter(WildPixelmonParticipant.class::isInstance)
                .map(WildPixelmonParticipant.class::cast)
                .findFirst()
                .orElse(null);

        if (participants.size() == 2 && player != null && wildPixelmon != null && player.getPlayer() != null
            && wildPixelmon.getEntity() != null && hasLegendaryPlayerOwner(wildPixelmon.getEntity().getUUID(), player.getPlayer())) {
            if (!NeoForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender((PixelmonEntity) wildPixelmon.getEntity(), player.getPlayer())).isCanceled()) {
                player.getPlayer().sendSystemMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocaleConfig().getIncorrectCause()));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onStartCapture(CaptureEvent.StartCapture event) {
        val player = event.getPlayer();

        if (hasLegendaryPlayerOwner(event.getPokemon().getUUID(), player)) {
            if (!NeoForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender(
                    event.getPokemon().getPixelmonEntity().orElse(null), player)).isCanceled()) {
                if (!player.isCreative()) {
                    player.getInventory().add(event.getPokeBall().getBallItem());
                }

                player.sendSystemMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocaleConfig().getIncorrectCause()));
                event.setCanceled(true);
            }
        }
    }
}
