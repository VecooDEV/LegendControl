package com.vecoo.legendcontrol.listener;

import com.envyful.api.forge.chat.UtilChatColour;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.KeyEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendaryCheckSpawnsEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.comm.packetHandlers.EnumKeyPacketMode;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.items.ItemPokeball;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.storage.player.PlayerFactory;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import com.vecoo.legendcontrol.util.Task;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LegendControlListener {
    public static HashMap<EntityPixelmon, UUID> legendMap = new HashMap<>();

    private boolean hasMap(EntityPlayerMP player, EntityPixelmon pixelmon) {
        if (legendMap.containsKey(pixelmon) && !player.isCreative()) {
            UUID invokerUUID = legendMap.get(pixelmon);

            if (!invokerUUID.equals(player.getUniqueID()) && !PlayerFactory.hasPlayer(invokerUUID, player.getUniqueID())) {
                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getIncorrectCause())));
                return false;
            }
        }
        return true;
    }

    private static boolean isBlackListed(Pokemon pokemon) {
        for (PokemonSpec blocked : LegendControl.getInstance().getConfig().getBlockedLegendary()) {
            if (blocked != null && blocked.matches(pokemon)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onLegendarySpawnControl(LegendarySpawnEvent.DoSpawn event) {
        if (LegendControl.getInstance().getConfig().isNewLegendarySpawn()) {
            ServerConfig config = LegendControl.getInstance().getConfig();
            EntityPlayerMP player = (EntityPlayerMP) event.action.spawnLocation.cause;
            EntityPixelmon pokemon = event.action.getOrCreateEntity();

            if (ThreadLocalRandom.current().nextInt(100) > ServerFactory.getLegendaryChance() && config.isNewLegendarySpawn()) {
                ServerFactory.addLegendaryChance(config.getStepSpawnChance());
                event.setCanceled(true);
                return;
            }

            if (isBlackListed(event.action.getOrCreateEntity().getPokemonData())) {
                ServerFactory.addLegendaryChance(config.getStepSpawnChance());
                event.setCanceled(true);
                return;
            }

            if (!config.isLegendaryRepeat() && ServerFactory.getLastLegend().equals(pokemon.getName())) {
                ServerFactory.addLegendaryChance(config.getStepSpawnChance());
                event.setCanceled(true);
                return;
            }

            if (!config.isRepeatSpawnToPlayer() && ServerFactory.getPlayersIP().contains(player.getPlayerIP())) {
                ServerFactory.addLegendaryChance(config.getStepSpawnChance());
                event.setCanceled(true);
                return;
            }

            if (LegendControl.getInstance().getConfig().isNotifyLegendarySpawn()) {
                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getSpawnPlayerLegendary())));
            }

            legendMap.put(pokemon, player.getUniqueID());

            ServerFactory.setLegendaryChance(config.getBaseChance());
            ServerFactory.setLastLegend(pokemon.getName());
            ServerFactory.replacePlayerIP(player.getPlayerIP());

            int num = config.getProtectedTime();

            if (num > 0 && config.isLegendaryDefender()) {
                Task.builder()
                        .execute(() -> {
                            LegendControl.getInstance().getServer().getPlayerList().sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                                    LegendControl.getInstance().getLocale().getMessages().getProtection()
                                            .replace("%pokemon%", pokemon.getSpecies().getLocalizedName()))));
                            legendMap.remove(pokemon);
                        })
                        .delay(20L * num)
                        .interval(20L * num)
                        .build();
            }
        }
    }

    @SubscribeEvent
    public void onBattleKey(KeyEvent event) {
        if (event.key == EnumKeyPacketMode.ActionKeyEntity && !legendMap.isEmpty() && LegendControl.getInstance().getConfig().isLegendaryDefender()) {
            for (Pokemon pokemon : Pixelmon.storageManager.getParty(event.player).getTeam()) {
                pokemon.ifEntityExists(entityPixelmon -> {
                    Entity target = entityPixelmon.getAttackTarget();
                    if (target instanceof EntityPixelmon) {
                        if (!hasMap(event.player, (EntityPixelmon) target)) {
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

                if (!hasMap((EntityPlayerMP) playerParticipant.getEntity(), (EntityPixelmon) wildPokemon.getEntity())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.StartCapture event) {
        if (LegendControl.getInstance().getConfig().isLegendaryDefender()) {
            EntityPlayerMP player = event.player;

            if (!hasMap(player, event.getPokemon())) {
                event.setCanceled(true);
                ItemPokeball pokeball = event.pokeball.getType().getItem();
                player.inventory.addItemStackToInventory(new ItemStack(pokeball, 1));
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