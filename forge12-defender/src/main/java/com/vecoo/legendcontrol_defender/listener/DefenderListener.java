package com.vecoo.legendcontrol_defender.listener;

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
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.LegendControlDefenderEvent;
import com.vecoo.legendcontrol_defender.api.factory.LegendControlFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    public boolean hasLegendaryPlayerOwner(UUID pokemonUUID, EntityPlayerMP player) {
        if (!hasLegendaryDefender(pokemonUUID)) {
            return false;
        }

        UUID ownerUUID = LEGENDARY_DEFENDER.get(pokemonUUID);

        if (ownerUUID.equals(player.getUniqueID())) {
            return false;
        }

        return !LegendControlFactory.PlayerProvider.hasPlayerTrust(ownerUUID, player.getUniqueID());
    }

    @SubscribeEvent
    public void onDoSpawn(LegendarySpawnEvent.DoSpawn event) {
        EntityPixelmon entityPixelmon = event.action.getOrCreateEntity();

        addLegendaryDefender(entityPixelmon.getUniqueID(), event.action.spawnLocation.cause.getUniqueID());

        TaskTimer.builder()
                .delay(1L)
                .consume(task -> {
                    if (entityPixelmon.isEntityAlive()) {
                        if (LegendControlDefender.getInstance().getConfig().getProtectedTime() > 0) {
                            startDefender(entityPixelmon);
                        }
                    }
                }).build();
    }

    private void startDefender(EntityPixelmon entityPixelmon) {
        TaskTimer.builder()
                .delay(LegendControlDefender.getInstance().getConfig().getProtectedTime() * 20L)
                .consume(task -> {
                    if (hasLegendaryDefender(entityPixelmon.getUniqueID()) && entityPixelmon.isEntityAlive() && !entityPixelmon.hasOwner()) {
                        UtilChat.broadcast(LegendControlDefender.getInstance().getLocale().getProtection()
                                .replace("%pokemon%", entityPixelmon.getPokemonName()));

                        MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.ExpiredDefender(entityPixelmon));

                        removeLegendaryDefender(entityPixelmon.getUniqueID());
                    }
                }).build();
    }

    @SubscribeEvent
    public void onKey(KeyEvent event) {
        if (event.key != EnumKeyPacketMode.ActionKeyEntity) {
            return;
        }

        EntityPlayerMP player = event.player;

        for (Pokemon pokemon : Pixelmon.storageManager.getParty(player).getTeam()) {
            pokemon.ifEntityExists(pixelmonEntity -> {
                Entity target = pixelmonEntity.getAttackTarget();

                if (target instanceof EntityPixelmon && hasLegendaryPlayerOwner(target.getUniqueID(), player) && !MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender(pixelmonEntity, player))) {
                    player.sendMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getIncorrectCause()));
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

        List<BattleParticipant> participants = event.bc.participants;

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

        if (participants.size() == 2 && player != null && wildPixelmon != null && hasLegendaryPlayerOwner(wildPixelmon.getEntity().getUniqueID(), (EntityPlayerMP) player.getEntity())) {
            if (!MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender((EntityPixelmon) wildPixelmon.getEntity(), (EntityPlayerMP) player.getEntity()))) {
                player.getEntity().sendMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getIncorrectCause()));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onStartCapture(CaptureEvent.StartCapture event) {
        EntityPlayerMP player = event.player;

        if (hasLegendaryPlayerOwner(event.getPokemon().getUniqueID(), player)) {
            if (!MinecraftForge.EVENT_BUS.post(new LegendControlDefenderEvent.WorkedDefender(event.getPokemon(), player))) {
                if (!player.isCreative()) {
                    player.inventory.addItemStackToInventory(new ItemStack(event.pokeball.getType().getItem(), 1));
                }

                player.sendMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getIncorrectCause()));
                event.setCanceled(true);
            }
        }
    }
}