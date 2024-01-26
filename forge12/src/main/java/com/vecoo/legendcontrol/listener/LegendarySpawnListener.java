package com.vecoo.legendcontrol.listener;

import com.envyful.api.forge.chat.UtilChatColour;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.KeyEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendaryCheckSpawnsEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.comm.packetHandlers.EnumKeyPacketMode;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.items.ItemPokeball;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.utils.Task;
import com.vecoo.legendcontrol.utils.data.UtilsTrust;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LegendarySpawnListener {

    public static HashMap<EntityPixelmon, UUID> legendMap = new HashMap<>();
    public static int legendaryChance;

    private boolean checkLegendMap(EntityPlayerMP player, EntityPixelmon pixelmon) {
        try {
            if (legendMap.containsKey(pixelmon) && !player.isCreative()) {
                UUID invokerUUID = legendMap.get(pixelmon);
                List<UUID> allowedPlayers = new ArrayList<>();

                if (UtilsTrust.getDataMap().containsKey(invokerUUID)) {
                    allowedPlayers.addAll(UtilsTrust.getDataMap().get(invokerUUID));
                }

                if (!invokerUUID.equals(player.getUniqueID()) && !allowedPlayers.contains(player.getUniqueID())) {
                    player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                            LegendControl.getInstance().getLocale().getMessages().getIncorrectCause())));
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SubscribeEvent
    public void onLegendarySpawnControl(LegendarySpawnEvent.DoSpawn event) {
        if (LegendControl.getInstance().getConfig().isNewLegendarySpawn()) {
            EntityPixelmon spawnEntity = event.action.getOrCreateEntity();
            EntityPlayerMP player = (EntityPlayerMP) event.action.spawnLocation.cause;

            int random = ThreadLocalRandom.current().nextInt(100);
            int num = LegendControl.getInstance().getConfig().getLegendProtectedTime();

            if (random > legendaryChance) {
                legendaryChance += LegendControl.getInstance().getConfig().getStepSpawnChance();
                event.setCanceled(true);
            } else {
                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getSpawnPlayerLegendary())));
                legendMap.put(spawnEntity, player.getUniqueID());
                legendaryChance = LegendControl.getInstance().getConfig().getBaseChance();

                if (num > 0 && LegendControl.getInstance().getConfig().isLegendaryDefender()) {
                    Task.builder()
                            .execute(() -> {
                                legendMap.remove(spawnEntity);
                                if (spawnEntity.hasOwner() || spawnEntity.isDead) {
                                    return;
                                }
                                FMLServerHandler.instance().getServer().getPlayerList().sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                                        LegendControl.getInstance().getLocale().getMessages().getProtection()
                                                .replace("%pokemon%", spawnEntity.getSpecies().name))));
                            })
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
            EntityPlayerMP player = (EntityPlayerMP) event.action.spawnLocation.cause;
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getSpawnPlayerLegendary())));
        }
    }

    @SubscribeEvent
    public void onBattleKey(KeyEvent event) {
        if (event.key == EnumKeyPacketMode.ActionKeyEntity && !legendMap.isEmpty()) {
            EntityPlayerMP player = event.player;

            for (Pokemon pokemon : Pixelmon.storageManager.getParty(event.player).getTeam()) {
                pokemon.ifEntityExists(entityPixelmon -> {
                    final Entity target = entityPixelmon.getAttackTarget();
                    if (target instanceof EntityPixelmon) {
                        final EntityPixelmon targetPoke = (EntityPixelmon) target;
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
        BattleParticipant participant1 = event.participant1[0];
        BattleParticipant participant2 = event.participant2[0];
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

            EntityPlayerMP player = (EntityPlayerMP) playerParticipant.getEntity();
            EntityPixelmon pixelmon = (EntityPixelmon) wildPokemon.getEntity();

            if (checkLegendMap(player, pixelmon)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.StartCapture event) {
        EntityPlayerMP player = event.player;
        EntityPixelmon pixelmon = event.getPokemon();

        if (checkLegendMap(player, pixelmon)) {
            event.setCanceled(true);
            ItemPokeball pokeball = event.pokeball.getType().getItem();
            player.inventory.addItemStackToInventory(new ItemStack(pokeball, 1));
        }
    }

    @SubscribeEvent
    public void onCheckSpawns(LegendaryCheckSpawnsEvent event) {
        event.shouldShowChance = false;
    }
}
