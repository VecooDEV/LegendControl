package com.vecoo.legendcontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.player.LegendPlayerFactory;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.common.UsernameCache;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        for (String command : Arrays.asList("legendarytrust", "ltrust")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> p.hasPermission(LegendControl.getInstance().getPermission().getPermissionCommand().get("minecraft.command.legendarytrust")))
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

    private static int executeAdd(ServerPlayerEntity player, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> trustedPlayers = LegendPlayerFactory.getPlayersTrust(player.getUUID());

        if (player.getUUID().equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getCantSelfTrust()), Util.NIL_UUID);
            return 0;
        }

        if (trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAlreadyTrusted()), Util.NIL_UUID);
            return 0;
        }

        if (trustedPlayers.size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getTrustLimit()), Util.NIL_UUID);
            return 0;
        }

        LegendPlayerFactory.addPlayerTrust(player.getUUID(), targetUUID);

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAddTrust()
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemove(ServerPlayerEntity player, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> trustedPlayers = LegendPlayerFactory.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getNotPlayerTrust()), Util.NIL_UUID);
            return 0;
        }

        LegendPlayerFactory.removePlayerTrust(player.getUUID(), targetUUID);

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveTrust()
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemoveAll(ServerPlayerEntity player) {
        if (LegendPlayerFactory.getPlayersTrust(player.getUUID()).isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        LegendPlayerFactory.removePlayersTrust(player.getUUID());

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveAllTrust()), Util.NIL_UUID);
        return 1;
    }

    private static int executeList(ServerPlayerEntity player) {
        List<UUID> players = LegendPlayerFactory.getPlayersTrust(player.getUUID());

        if (players.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getListTrust()), Util.NIL_UUID);

        for (UUID uuid : players) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getListPlayer()
                        .replace("%player%", playerName)), Util.NIL_UUID);
            }
        }
        return 1;
    }
}