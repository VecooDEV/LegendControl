package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extrasapi.chat.UtilChat;
import com.vecoo.extrasapi.player.UtilPlayer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.player.PlayerFactory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.UsernameCache;

import java.util.List;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String command : List.of("legendarytrust", "ltrust")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> p.hasPermission(0))
                    .then(Commands.literal("add")
                            .then(Commands.argument("player", StringArgumentType.string())
                                    .executes(e -> executeAdd(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player")))
                                    .suggests((s, builder) -> {
                                        for (String nick : s.getSource().getOnlinePlayerNames()) {
                                            builder.suggest(nick);
                                        }
                                        return builder.buildFuture();
                                    })))
                    .then(Commands.literal("remove")
                            .then(Commands.argument("player", StringArgumentType.string())
                                    .executes(e -> executeRemove(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player")))
                                    .suggests((s, builder) -> {
                                        for (String nick : s.getSource().getOnlinePlayerNames()) {
                                            builder.suggest(nick);
                                        }
                                        return builder.buildFuture();
                                    }))
                            .then(Commands.literal("all")
                                    .executes(e -> executeRemoveAll(e.getSource().getPlayerOrException()))))
                    .then(Commands.literal("list")
                            .executes(e -> executeList(e.getSource().getPlayerOrException()))));
        }
    }

    private static int executeAdd(ServerPlayer player, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%target%", target)));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> trustedPlayers = PlayerFactory.getPlayersTrust(player.getUUID());

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getCantSelfTrust()));
            return 0;
        }

        if (trustedPlayers.contains(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAlreadyTrusted()));
            return 0;
        }

        if (trustedPlayers.size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getTrustLimit()));
            return 0;
        }

        PlayerFactory.addPlayerTrust(player.getUUID(), targetUUID);

        player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAddTrust()
                .replace("%target%", target)));
        return 1;
    }

    private static int executeRemove(ServerPlayer player, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%target%", target)));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> trustedPlayers = PlayerFactory.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return 0;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getNotPlayerTrust()));
            return 0;
        }

        PlayerFactory.removePlayerTrust(player.getUUID(), targetUUID);

        player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveTrust()
                .replace("%target%", target)));
        return 1;
    }

    private static int executeRemoveAll(ServerPlayer player) {
        if (PlayerFactory.getPlayersTrust(player.getUUID()).isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return 0;
        }

        PlayerFactory.removePlayersTrust(player.getUUID());

        player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveAllTrust()));
        return 1;
    }

    private static int executeList(ServerPlayer player) {
        List<UUID> players = PlayerFactory.getPlayersTrust(player.getUUID());

        if (players.isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getListTrust()
                .replace("%amount%", players.size() + "")));

        for (UUID uuid : players) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                player.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getListPlayer()
                        .replace("%player%", playerName)));
            }
        }
        return 1;
    }
}