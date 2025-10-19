package com.vecoo.legendcontrol.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.text.UtilText;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import javax.annotation.Nonnull;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("lc")
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.lc"))
                .then(Commands.literal("add")
                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                .suggests((s, builder) -> {
                                    for (int chance : Sets.newHashSet(10, 25, 50)) {
                                        builder.suggest(chance);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeAdd(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                .suggests((s, builder) -> {
                                    for (int chance : Sets.newHashSet(10, 25, 50)) {
                                        builder.suggest(chance);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeRemove(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("set")
                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                .suggests((s, builder) -> {
                                    for (int chance : Sets.newHashSet(10, 50, 100)) {
                                        builder.suggest(chance);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeSet(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("reload")
                        .executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(@Nonnull CommandSource source, float chance) {
        if (LegendControlFactory.ServerProvider.getChanceLegend() + chance > 100F) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getErrorChance()), false);
            return 0;
        }

        if (!LegendControlFactory.ServerProvider.addChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", UtilText.getFormattedFloat(LegendControlFactory.ServerProvider.getChanceLegend()))), false);
        return 1;
    }

    private static int executeRemove(@Nonnull CommandSource source, float chance) {
        if (LegendControlFactory.ServerProvider.getChanceLegend() - chance < 0F) {
            source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getErrorChance()), false);
            return 0;
        }

        if (!LegendControlFactory.ServerProvider.removeChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", UtilText.getFormattedFloat(LegendControlFactory.ServerProvider.getChanceLegend()))), false);
        return 1;
    }

    private static int executeSet(@Nonnull CommandSource source, float chance) {
        if (!LegendControlFactory.ServerProvider.setChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", UtilText.getFormattedFloat(LegendControlFactory.ServerProvider.getChanceLegend()))), false);
        return 1;
    }

    private static int executeReload(@Nonnull CommandSource source) {
        LegendControl.getInstance().loadConfig();
        LegendControl.getInstance().loadStorage();

        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getReload()), false);
        return 1;
    }
}