package com.vecoo.legendcontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.text.UtilText;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.util.PermissionNodes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Arrays;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lc")
                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.LEGENDCONTROL_COMMAND))
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

    private static int executeAdd(CommandSourceStack source, float chance) {
        if (LegendControlFactory.ServerProvider.getChanceLegend() + chance > 100F) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getErrorChance()));
            return 0;
        }

        LegendControlFactory.ServerProvider.addChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", UtilText.getFormattedFloat(LegendControlFactory.ServerProvider.getChanceLegend()))));
        return 1;
    }

    private static int executeRemove(CommandSourceStack source, float chance) {
        if (LegendControlFactory.ServerProvider.getChanceLegend() - chance < 0F) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getErrorChance()));
            return 0;
        }

        LegendControlFactory.ServerProvider.removeChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", UtilText.getFormattedFloat(LegendControlFactory.ServerProvider.getChanceLegend()))));
        return 1;
    }

    private static int executeSet(CommandSourceStack source, float chance) {
        LegendControlFactory.ServerProvider.setChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance);

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", UtilText.getFormattedFloat(LegendControlFactory.ServerProvider.getChanceLegend()))));
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        LegendControl.getInstance().loadConfig();

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getReload()));
        return 1;
    }
}