package com.vecoo.legendcontrol.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.server.UtilCommand;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.util.PermissionNodes;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lc")
                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.LEGENDCONTROL_COMMAND))
                .then(Commands.literal("add")
                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                .suggests(UtilCommand.suggestAmount(Sets.newHashSet(10, 25, 50)))
                                .executes(e -> executeAdd(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                .suggests(UtilCommand.suggestAmount(Sets.newHashSet(10, 25, 50)))
                                .executes(e -> executeRemove(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("set")
                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                .suggests(UtilCommand.suggestAmount(Sets.newHashSet(10, 50, 100)))
                                .executes(e -> executeSet(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("reload")
                        .executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(@NotNull CommandSourceStack source, float chance) {
        if (LegendControlFactory.ServerProvider.getChanceLegend() + chance > 100F) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocaleConfig().getErrorChance()));
            return 0;
        }

        if (!LegendControlFactory.ServerProvider.addChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return 0;
        }

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocaleConfig().getChangeChanceLegendary()
                .replace("%chance%", Utils.formatFloat(LegendControlFactory.ServerProvider.getChanceLegend()))));
        return 1;
    }

    private static int executeRemove(@NotNull CommandSourceStack source, float chance) {
        if (LegendControlFactory.ServerProvider.getChanceLegend() - chance < 0F) {
            source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocaleConfig().getErrorChance()));
            return 0;
        }

        if (!LegendControlFactory.ServerProvider.removeChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return 0;
        }

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocaleConfig().getChangeChanceLegendary()
                .replace("%chance%", Utils.formatFloat(LegendControlFactory.ServerProvider.getChanceLegend()))));
        return 1;
    }

    private static int executeSet(@NotNull CommandSourceStack source, float chance) {
        if (!LegendControlFactory.ServerProvider.setChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return 0;
        }

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocaleConfig().getChangeChanceLegendary()
                .replace("%chance%", Utils.formatFloat(LegendControlFactory.ServerProvider.getChanceLegend()))));
        return 1;
    }

    private static int executeReload(@NotNull CommandSourceStack source) {
        LegendControl.getInstance().loadConfig();
        LegendControl.getInstance().loadStorage();

        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocaleConfig().getReload()));
        return 1;
    }
}