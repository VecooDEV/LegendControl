package com.vecoo.legendcontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.LegendServerFactory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.UsernameCache;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String command : List.of("legendcontrol", "lc")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> p.hasPermission(LegendControl.getInstance().getPermission().getPermissionCommand().get("minecraft.command.legendcontrol")))
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

    private static int executeAdd(CommandSourceStack source, float chance) {
        if (LegendServerFactory.getLegendaryChance() + chance > 100F) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()));
            return 0;
        }

        LegendServerFactory.addLegendaryChance(chance);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", LegendServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")));
        return 1;
    }

    private static int executeRemove(CommandSourceStack source, float chance) {
        if (LegendServerFactory.getLegendaryChance() - chance < 0F) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()));
            return 0;
        }

        LegendServerFactory.removeLegendaryChance(chance);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", LegendServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")));
        return 1;
    }

    private static int executeSet(CommandSourceStack source, float chance) {
        LegendServerFactory.setLegendaryChance(chance);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", LegendServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")));
        return 1;
    }

    private static int executeBlacklist(CommandSourceStack source) {
        List<UUID> playersBlacklist = LegendServerFactory.getPlayersBlacklist();

        if (playersBlacklist.isEmpty()) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyBlacklist()));
            return 0;
        }

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getBlacklist()));

        for (UUID uuid : playersBlacklist) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getListPlayer()
                        .replace("%player%", playerName)));
            }
        }
        return 1;
    }

    private static int executeBlacklistAdd(CommandSourceStack source, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> playersBlacklist = LegendServerFactory.getPlayersBlacklist();

        if (playersBlacklist.contains(targetUUID)) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAlreadyBlacklist()));
            return 0;
        }

        LegendServerFactory.addPlayerBlacklist(targetUUID);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getAddBlacklist()
                .replace("%player%", target)));
        return 1;
    }

    private static int executeBlacklistRemove(CommandSourceStack source, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> playersBlacklist = LegendServerFactory.getPlayersBlacklist();

        if (playersBlacklist.isEmpty()) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyBlacklist()));
            return 0;
        }

        if (!playersBlacklist.contains(targetUUID)) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getNotPlayerBlacklist()));
            return 0;
        }

        LegendServerFactory.removePlayerBlacklist(targetUUID);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveBlacklist()
                .replace("%player%", target)));
        return 1;
    }

    private static int executeBlacklistRemoveAll(CommandSourceStack source) {
        if (LegendServerFactory.getPlayersBlacklist().isEmpty()) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return 0;
        }

        LegendServerFactory.removePlayersBlacklist();

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getRemoveAllBlacklist()));
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        LegendControl.getInstance().loadConfig();
        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getReload()));
        return 1;
    }
}