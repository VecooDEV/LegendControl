package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.util.UtilChatColour;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lc").requires((p -> Utils.hasPermission(p.getPlayer(), "command.main")))
                .then(Commands.literal("add").then(Commands.argument("amount", IntegerArgumentType.integer(0, 100)).
                        executes(e -> executeAdd(e.getSource(), IntegerArgumentType.getInteger(e, "amount")))))
                .then(Commands.literal("set").then(Commands.argument("amount", IntegerArgumentType.integer(0, 100)).
                        executes(e -> executeSet(e.getSource(), IntegerArgumentType.getInteger(e, "amount")))))
                .then(Commands.literal("reload").executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(CommandSourceStack source, int chance) {
        if (LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance() + chance > 100) {
            source.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getErrorChance()));
            return 0;
        }

        LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().addChance(chance);

        source.sendSystemMessage(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                        .replace("%chance%", LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance() + "%")));
        return 1;
    }

    private static int executeSet(CommandSourceStack source, int chance) {
        LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().setChance(chance);
        source.sendSystemMessage(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                        .replace("%chance%", chance + "%")));
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        LegendControl.getInstance().loadConfig();
        source.sendSystemMessage(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getReload()));
        return 1;
    }
}