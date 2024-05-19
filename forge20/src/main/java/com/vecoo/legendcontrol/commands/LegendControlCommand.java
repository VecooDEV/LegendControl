package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lc").requires(p -> p.hasPermission(2))
                .then(Commands.literal("add").then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F)).
                        executes(e -> executeAdd(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("remove").then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F)).
                        executes(e -> executeRemove(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("set").then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F)).
                        executes(e -> executeSet(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("reload").executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(CommandSourceStack source, float chance) {
        if (ServerFactory.getLegendaryChance() + chance > 100F) {
            source.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()));
            return 0;
        }

        ServerFactory.addLegendaryChance(chance);

        source.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f",ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "")+ "%")));
        return 1;
    }

    private static int executeRemove(CommandSourceStack source, float chance) {
        if (ServerFactory.getLegendaryChance() - chance < 0F) {
            source.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()));
            return 0;
        }

        ServerFactory.removeLegendaryChance(chance);

        source.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f",ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "")+ "%")));
        return 1;
    }

    private static int executeSet(CommandSourceStack source, float chance) {
        ServerFactory.setLegendaryChance(chance);
        source.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f",ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "")+ "%")));
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        LegendControl.getInstance().loadConfig();
        source.sendSystemMessage(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getReload()));
        return 1;
    }
}