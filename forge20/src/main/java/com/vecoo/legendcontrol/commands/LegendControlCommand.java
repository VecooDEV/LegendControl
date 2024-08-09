package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extrasapi.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String command : List.of("legendcontrol", "lc")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> p.hasPermission(2))
                    .then(Commands.literal("add")
                            .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                    .executes(e -> executeAdd(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                    .then(Commands.literal("remove")
                            .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                    .executes(e -> executeRemove(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                    .then(Commands.literal("set")
                            .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                    .executes(e -> executeSet(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                    .then(Commands.literal("blacklist")
                            .executes(e -> executeBlacklist(e.getSource()))
                            .then(Commands.literal("add")
                                    .then(Commands.argument("player", StringArgumentType.string())
                                            .executes(e -> executeBlacklistAdd(e.getSource(), StringArgumentType.getString(e, "player")))
                                            .suggests((s, builder) -> {
                                                for (String nick : s.getSource().getOnlinePlayerNames()) {
                                                    builder.suggest(nick);
                                                }
                                                return builder.buildFuture();
                                            })))
                            .then(Commands.literal("remove")
                                    .then(Commands.argument("player", StringArgumentType.string())
                                            .executes(e -> executeBlacklistRemove(e.getSource(), StringArgumentType.getString(e, "player")))
                                            .suggests((s, builder) -> {
                                                for (String nick : s.getSource().getOnlinePlayerNames()) {
                                                    builder.suggest(nick);
                                                }
                                                return builder.buildFuture();
                                            }))
                                    .then(Commands.literal("all")
                                            .executes(e -> executeBlacklistRemoveAll(e.getSource())))))
                    .then(Commands.literal("reload")
                            .executes(e -> executeReload(e.getSource()))));
        }
    }

    private static int executeAdd(CommandSourceStack source, float chance) {
        if (ServerFactory.getLegendaryChance() + chance > 100F) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()));
            return 0;
        }

        ServerFactory.addLegendaryChance(chance);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")));
        return 1;
    }

    private static int executeRemove(CommandSourceStack source, float chance) {
        if (ServerFactory.getLegendaryChance() - chance < 0F) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()));
            return 0;
        }

        ServerFactory.removeLegendaryChance(chance);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")));
        return 1;
    }

    private static int executeSet(CommandSourceStack source, float chance) {
        ServerFactory.setLegendaryChance(chance);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")));
        return 1;
    }

    private static int executeBlacklist(CommandSourceStack source) {
        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().));
    }

    private static int executeBlacklistAdd(CommandSourceStack source, String target) {

    }

    private static int executeBlacklistRemove(CommandSourceStack source, String target) {

    }

    private static int executeBlacklistRemoveAll(CommandSourceStack source) {

    }

    private static int executeReload(CommandSourceStack source) {
        LegendControl.getInstance().loadConfig();
        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getReload()));
        return 1;
    }
}