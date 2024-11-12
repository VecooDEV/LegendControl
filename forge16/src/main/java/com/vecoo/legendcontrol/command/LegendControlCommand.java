package com.vecoo.legendcontrol.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermissions;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.LegendServerFactory;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.common.UsernameCache;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        for (String command : Lists.newArrayList("legendcontrol", "lc")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> UtilPermissions.hasPermission(p, "minecraft.command.legendcontrol", LegendControl.getInstance().getPermission().getPermissionCommand()))
                    .then(Commands.literal("add")
                            .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                    .suggests((s, builder) -> {
                                        for (int chance : Arrays.asList(10, 25, 50)) {
                                            builder.suggest(chance);
                                        }
                                        return builder.buildFuture();
                                    })
                                    .executes(e -> executeAdd(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                    .then(Commands.literal("remove")
                            .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                    .suggests((s, builder) -> {
                                        for (int chance : Arrays.asList(10, 25, 50)) {
                                            builder.suggest(chance);
                                        }
                                        return builder.buildFuture();
                                    })
                                    .executes(e -> executeRemove(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                    .then(Commands.literal("set")
                            .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                    .suggests((s, builder) -> {
                                        for (int chance : Arrays.asList(10, 50, 100)) {
                                            builder.suggest(chance);
                                        }
                                        return builder.buildFuture();
                                    })
                                    .executes(e -> executeSet(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                    .then(Commands.literal("blacklist")
                            .then(Commands.literal("add")
                                    .then(Commands.argument("player", StringArgumentType.string())
                                            .suggests((s, builder) -> {
                                                for (String nick : s.getSource().getOnlinePlayerNames()) {
                                                    if (nick.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                        builder.suggest(nick);
                                                    }
                                                }
                                                return builder.buildFuture();
                                            })
                                            .executes(e -> executeBlacklistAdd(e.getSource(), StringArgumentType.getString(e, "player")))))
                            .then(Commands.literal("remove")
                                    .then(Commands.argument("player", StringArgumentType.string())
                                            .suggests((s, builder) -> {
                                                for (UUID uuid : LegendServerFactory.getPlayersBlacklist()) {
                                                    String name = UsernameCache.getLastKnownUsername(uuid);
                                                    if (name != null) {
                                                        if (name.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                            builder.suggest(name);
                                                        }
                                                    }
                                                }
                                                return builder.buildFuture();
                                            })
                                            .executes(e -> executeBlacklistRemove(e.getSource(), StringArgumentType.getString(e, "player"))))
                                    .then(Commands.literal("all")
                                            .executes(e -> executeBlacklistRemoveAll(e.getSource()))))
                            .then(Commands.literal("list")
                                    .executes(e -> executeBlacklist(e.getSource()))))
                    .then(Commands.literal("reload")
                            .executes(e -> executeReload(e.getSource()))));
        }
    }

    private static int executeAdd(CommandSource source, float chance) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.legendcontrol", LegendControl.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotPermission()), false);
            return 0;
        }

        if (LegendServerFactory.getLegendaryChance() + chance > 100F) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()), false);
            return 0;
        }

        LegendServerFactory.addLegendaryChance(chance);

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", LegendServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")), false);
        return 1;
    }

    private static int executeRemove(CommandSource source, float chance) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.legendcontrol", LegendControl.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotPermission()), false);
            return 0;
        }

        if (LegendServerFactory.getLegendaryChance() - chance < 0F) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()), false);
            return 0;
        }

        LegendServerFactory.removeLegendaryChance(chance);

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", LegendServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")), false);
        return 1;
    }

    private static int executeSet(CommandSource source, float chance) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.legendcontrol", LegendControl.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotPermission()), false);
            return 0;
        }

        LegendServerFactory.setLegendaryChance(chance);

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", LegendServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")), false);
        return 1;
    }

    private static int executeBlacklist(CommandSource source) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.legendcontrol", LegendControl.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotPermission()), false);
            return 0;
        }

        List<UUID> playersBlacklist = LegendServerFactory.getPlayersBlacklist();

        if (playersBlacklist.isEmpty()) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyBlacklist()), false);
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getBlacklist()), false);

        for (UUID uuid : playersBlacklist) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getListPlayer()
                        .replace("%player%", playerName)), false);
            }
        }
        return 1;
    }

    private static int executeBlacklistAdd(CommandSource source, String target) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.legendcontrol", LegendControl.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotPermission()), false);
            return 0;
        }

        if (!UtilPlayer.hasUUID(target)) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%player%", target)), false);
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> playersBlacklist = LegendServerFactory.getPlayersBlacklist();

        if (playersBlacklist.contains(targetUUID)) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAlreadyBlacklist()), false);
            return 0;
        }

        LegendServerFactory.addPlayerBlacklist(targetUUID);

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAddBlacklist()
                .replace("%player%", target)), false);
        return 1;
    }

    private static int executeBlacklistRemove(CommandSource source, String target) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.legendcontrol", LegendControl.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotPermission()), false);
            return 0;
        }

        if (!UtilPlayer.hasUUID(target)) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%player%", target)), false);
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> playersBlacklist = LegendServerFactory.getPlayersBlacklist();

        if (playersBlacklist.isEmpty()) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyBlacklist()), false);
            return 0;
        }

        if (!playersBlacklist.contains(targetUUID)) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getNotPlayerBlacklist()), false);
            return 0;
        }

        LegendServerFactory.removePlayerBlacklist(targetUUID);

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveBlacklist()
                .replace("%player%", target)), false);
        return 1;
    }

    private static int executeBlacklistRemoveAll(CommandSource source) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.legendcontrol", LegendControl.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotPermission()), false);
            return 0;
        }

        if (LegendServerFactory.getPlayersBlacklist().isEmpty()) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), false);
            return 0;
        }

        LegendServerFactory.removePlayersBlacklist();

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveAllBlacklist()), false);
        return 1;
    }

    private static int executeReload(CommandSource source) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.legendcontrol", LegendControl.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotPermission()), false);
            return 0;
        }

        LegendControl.getInstance().loadConfig();
        LegendControl.getInstance().loadStorage();

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getReload()), false);
        return 1;
    }
}