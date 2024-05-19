package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.player.PlayerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.UsernameCache;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ltrust").requires(p -> p.hasPermission(0))
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

    private static int executeAdd(ServerPlayer player, String target) {
        if (!Utils.hasUUID(target)) {
            player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%target%", target)));
            return 0;
        }

        UUID targerUUID = Utils.getUUID(target);
        List<UUID> players = PlayerFactory.getPlayers(player.getUUID());

        if (player.getUUID().equals(targerUUID)) {
            player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getCantSelfTrust()));
            return 0;
        }

        if (players.contains(targerUUID)) {
            player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAlreadyTrusted()));
            return 0;
        }

        if (players.size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
            player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getTrustLimit()));
            return 0;
        }

        PlayerFactory.addPlayer(player.getUUID(), targerUUID);

        player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAddTrust()
                .replace("%target%", target)));
        return 1;
    }

    private static int executeRemove(ServerPlayer player, String target) {
        if (!Utils.hasUUID(target)) {
            player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%target%", target)));
            return 0;
        }

        UUID targetUUID = Utils.getUUID(target);
        List<UUID> players = PlayerFactory.getPlayers(player.getUUID());

        if (players.isEmpty()) {
            player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return 0;
        }

        if (!players.contains(targetUUID)) {
            player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getNotPlayerTrust()));
            return 0;
        }

        PlayerFactory.removePlayer(player.getUUID(), targetUUID);

        player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveTrust()
                .replace("%target%", target)));
        return 1;
    }

    private static int executeRemoveAll(ServerPlayer player) {
        List<UUID> players = PlayerFactory.getPlayers(player.getUUID());

        if (players.isEmpty()) {
            player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return 0;
        }

        Iterator<UUID> iterator = players.iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            iterator.remove();
            PlayerFactory.removePlayer(player.getUUID(), uuid);
        }

        player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveAllTrust()));
        return 1;
    }

    private static int executeList(ServerPlayer player) {
        List<UUID> players = PlayerFactory.getPlayers(player.getUUID());

        if (players.isEmpty()) {
            player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return 0;
        }

        player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getListTrustTitle()
                .replace("%amount%", players.size() + "")));

        for (UUID uuid : players) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                player.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getListTrust()
                        .replace("%player%", playerName)));
            }
        }
        return 1;
    }
}