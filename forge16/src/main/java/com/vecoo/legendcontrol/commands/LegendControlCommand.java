package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("lc").requires(p -> p.hasPermission(2))
                .then(Commands.literal("add").then(Commands.argument("chance", IntegerArgumentType.integer(0, 100)).
                        executes(e -> executeAdd(e.getSource(), IntegerArgumentType.getInteger(e, "chance")))))
                .then(Commands.literal("set").then(Commands.argument("chance", IntegerArgumentType.integer(0, 100)).
                        executes(e -> executeSet(e.getSource(), IntegerArgumentType.getInteger(e, "chance")))))
                .then(Commands.literal("reload").executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(CommandSource source, int chance) {
        if (ServerFactory.getLegendaryChance() + chance > 100) {
            source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()), false);
            return 0;
        }

        ServerFactory.addLegendaryChance(chance);

        source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", ServerFactory.getLegendaryChance() + "%")), false);
        return 1;
    }

    private static int executeSet(CommandSource source, int chance) {
        ServerFactory.setLegendaryChance(chance);
        source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", chance + "%")), false);
        return 1;
    }

    private static int executeReload(CommandSource source) {
        LegendControl.getInstance().loadConfig();
        source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getReload()), false);
        return 1;
    }
}