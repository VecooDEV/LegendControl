package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.player.PlayerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.common.UsernameCache;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("ltrust").requires(p -> p.hasPermission(2))
                .then(Commands.literal("add").then(Commands.argument("player", StringArgumentType.string()).
                        executes(e -> executeAdd(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player")))
                        .suggests((s, builder) -> {
                            for (String nick : s.getSource().getOnlinePlayerNames()) {
                                builder.suggest(nick);
                            }
                            return builder.buildFuture();
                        })))
                .then(Commands.literal("remove").then(Commands.argument("player", StringArgumentType.string()).
                                executes(e -> executeRemove(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player")))
                                .suggests((s, builder) -> {
                                    for (String nick : s.getSource().getOnlinePlayerNames()) {
                                        builder.suggest(nick);
                                    }
                                    return builder.buildFuture();
                                }))
                        .then(Commands.literal("all").executes(e -> executeRemoveAll(e.getSource().getPlayerOrException()))))
                .then(Commands.literal("list").executes(e -> executeList(e.getSource().getPlayerOrException()))));
    }

    private static int executeAdd(ServerPlayerEntity player, String target) {
        if (!Utils.hasUUID(target)) {
            player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%target%", target)), Util.NIL_UUID);
            return 0;
        }

        UUID targerUUID = Utils.getUUID(target);
        List<UUID> players = PlayerFactory.getPlayers(player.getUUID());

        if (player.getUUID().equals(targerUUID)) {
            player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getCantSelfTrust()), Util.NIL_UUID);
            return 0;
        }

        if (players.contains(targerUUID)) {
            player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAlreadyTrusted()), Util.NIL_UUID);
            return 0;
        }

        if (players.size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
            player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getTrustLimit()), Util.NIL_UUID);
            return 0;
        }

        PlayerFactory.addPlayer(player.getUUID(), targerUUID);

        player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAddTrust()
                .replace("%target%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemove(ServerPlayerEntity player, String target) {
        if (!Utils.hasUUID(target)) {
            player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%target%", target)), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = Utils.getUUID(target);
        List<UUID> players = PlayerFactory.getPlayers(player.getUUID());

        if (players.isEmpty()) {
            player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        if (!players.contains(targetUUID)) {
            player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getNotPlayerTrust()), Util.NIL_UUID);
            return 0;
        }

        PlayerFactory.removePlayer(player.getUUID(), targetUUID);

        player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveTrust()
                .replace("%target%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemoveAll(ServerPlayerEntity player) {
        List<UUID> players = PlayerFactory.getPlayers(player.getUUID());

        if (players.isEmpty()) {
            player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        Iterator<UUID> iterator = players.iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            iterator.remove();
            PlayerFactory.removePlayer(player.getUUID(), uuid);
        }

        player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveAllTrust()), Util.NIL_UUID);
        return 1;
    }

    private static int executeList(ServerPlayerEntity player) {
        List<UUID> players = PlayerFactory.getPlayers(player.getUUID());

        if (players.isEmpty()) {
            player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getListTrustTitle()
                .replace("%amount%", players.size() + "")), Util.NIL_UUID);

        for (UUID uuid : players) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                player.sendMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getListTrust()
                        .replace("%player%", playerName)), Util.NIL_UUID);
            }
        }
        return 1;
    }
}