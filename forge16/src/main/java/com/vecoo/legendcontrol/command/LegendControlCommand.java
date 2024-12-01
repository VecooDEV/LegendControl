package com.vecoo.legendcontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.LegendFactory;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import java.util.Arrays;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("lc")
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.lc"))
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
                .then(Commands.literal("reload")
                        .executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(CommandSource source, float chance) {
        if (LegendFactory.getLegendaryChance() + chance > 100F) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()), false);
            return 0;
        }

        LegendFactory.addLegendaryChance(chance);

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", LegendFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")), false);
        return 1;
    }

    private static int executeRemove(CommandSource source, float chance) {
        if (LegendFactory.getLegendaryChance() - chance < 0F) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()), false);
            return 0;
        }

        LegendFactory.removeLegendaryChance(chance);

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", LegendFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")), false);
        return 1;
    }

    private static int executeSet(CommandSource source, float chance) {
        LegendFactory.setLegendaryChance(chance);

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", LegendFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")), false);
        return 1;
    }

    private static int executeReload(CommandSource source) {
        LegendControl.getInstance().loadConfig();
        LegendControl.getInstance().loadStorage();

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getReload()), false);
        return 1;
    }
}