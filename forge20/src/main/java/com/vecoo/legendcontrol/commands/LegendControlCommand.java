package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lc").requires(p -> p.hasPermission(2))
                .then(Commands.literal("add").then(Commands.argument("chance", IntegerArgumentType.integer(0, 100)).
                        executes(e -> executeAdd(e.getSource(), IntegerArgumentType.getInteger(e, "chance")))))
                .then(Commands.literal("set").then(Commands.argument("chance", IntegerArgumentType.integer(0, 100)).
                        executes(e -> executeSet(e.getSource(), IntegerArgumentType.getInteger(e, "chance")))))
                .then(Commands.literal("reload").executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(CommandSourceStack source, int chance) {
        if (ServerFactory.getLegendaryChance() + chance > 100) {
            source.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()));
            return 0;
        }

        ServerFactory.addLegendaryChance(chance);

        source.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", ServerFactory.getLegendaryChance() + "%")));
        return 1;
    }

    private static int executeSet(CommandSourceStack source, int chance) {
        ServerFactory.setLegendaryChance(chance);
        source.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", chance + "%")));
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        LegendControl.getInstance().loadConfig();
        source.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getReload()));
        return 1;
    }
}