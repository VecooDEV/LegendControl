package com.vecoo.legendcontrol_defender.listener;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.KeyEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.comm.packetHandlers.EnumKeyPacketMode;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.LegendControlDefenderEvent;
import com.vecoo.legendcontrol_defender.api.service.LegendControlService;
import com.vecoo.legendcontrol_defender.util.WebhookUtils;
import lombok.val;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefenderListener {
    private final Map<UUID, UUID> LEGENDARY_DEFENDER = new HashMap<>();

    public boolean hasLegendaryDefender(@Nonnull UUID pokemonUUID) {
        return LEGENDARY_DEFENDER.containsKey(pokemonUUID);
    }

    public boolean addLegendaryDefender(@Nonnull UUID pokemonUUID, @Nonnull UUID playerUUID) {
        if (hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        LEGENDARY_DEFENDER.put(pokemonUUID, playerUUID);
        return true;
    }

    public boolean removeLegendaryDefender(@Nonnull UUID pokemonUUID) {
        if (!hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        LEGENDARY_DEFENDER.remove(pokemonUUID);
        return true;
    }

    public boolean hasLegendaryPlayerOwner(@Nonnull UUID pokemonUUID, @Nonnull EntityPlayerMP player) {
        if (!hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        val ownerUUID = LEGENDARY_DEFENDER.get(pokemonUUID);

        if (ownerUUID.equals(player.getUniqueID())) {
            return false;
        }

        return !LegendControlService.hasPlayerTrust(ownerUUID, player.getUniqueID());
    }

    @SubscribeEvent
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        val entityPixelmon = event.action.getOrCreateEntity();

        addLegendaryDefender(entityPixelmon.getUniqueID(), event.action.spawnLocation.cause.getUniqueID());

        if (LegendControlDefender.getInstance().getServerConfig().getProtectedTime() > 0) {
            startDefender(entityPixelmon);
        }
    }

    private void startDefender(@Nonnull EntityPixelmon entityPixelmon) {
        TaskTimer.builder()
                .delay(LegendControlDefender.getInstance().getServerConfig().getProtectedTime() * 20L)
                .consume(task -> {
                    if (hasLegendaryDefender(entityPixelmon.getUniqueID()) && entityPixelmon.isEntityAlive() && !entityPixelmon.hasOwner()) {
                        val event = new LegendControlDefenderEvent.ExpiredDefender(entityPixelmon);

                        if (!event.isCanceled()) {
                            UtilChat.broadcast(LegendControlDefender.getInstance().getLocaleConfig().getProtection()
                                    .replace("%pokemon%", entityPixelmon.getPokemonName()));
                            WebhookUtils.defenderExpiredWebhook(entityPixelmon);
                        }
                    }
                }).build();
    }

    @SubscribeEvent
    public void onKey(KeyEvent event) {
        if (event.key != EnumKeyPacketMode.ActionKeyEntity) {
            return;
        }

        val player = event.player;

        for (Pokemon pokemon : Pixelmon.storageManager.getParty(player).getTeam()) {
            pokemon.ifEntityExists(pixelmonEntity -> {
                val target = pixelmonEntity.getAttackTarget();

                if (target instanceof EntityPixelmon && hasLegendaryPlayerOwner(target.getUniqueID(), player)
                    && !MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender(pixelmonEntity, player))) {
                    player.sendMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocaleConfig().getIncorrectCause()));
                    event.setCanceled(true);
                }
            });
        }
    }

    @SubscribeEvent
    public void onBattleStarted(BattleStartedEvent event) {
        if (event.bc.isPvP()) {
            return;
        }

        val participants = event.bc.participants;

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

        if (participants.size() == 2 && player != null && wildPixelmon != null
            && hasLegendaryPlayerOwner(wildPixelmon.getEntity().getUniqueID(), (EntityPlayerMP) player.getEntity())) {
            if (!MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender((EntityPixelmon) wildPixelmon.getEntity(), (EntityPlayerMP) player.getEntity()))) {
                player.getEntity().sendMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocaleConfig().getIncorrectCause()));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onStartCapture(CaptureEvent.StartCapture event) {
        val player = event.player;

        if (hasLegendaryPlayerOwner(event.getPokemon().getUniqueID(), player)) {
            if (!MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender(event.getPokemon(), player))) {
                if (!player.isCreative()) {
                    player.inventory.addItemStackToInventory(new ItemStack(event.pokeball.getType().getItem(), 1));
                }

                player.sendMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocaleConfig().getIncorrectCause()));
                event.setCanceled(true);
            }
        }
    }
}