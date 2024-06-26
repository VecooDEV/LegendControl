package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("lc").requires(p -> p.hasPermission(2))
                .then(Commands.literal("add").then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F)).
                        executes(e -> executeAdd(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("remove").then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F)).
                        executes(e -> executeRemove(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("set").then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F)).
                        executes(e -> executeSet(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))
                .then(Commands.literal("reload").executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(CommandSource source, float chance) {
        if (ServerFactory.getLegendaryChance() + chance > 100F) {
            source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()), false);
            return 0;
        }

        ServerFactory.addLegendaryChance(chance);

        source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f",ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "")+ "%")), false);
        return 1;
    }

    private static int executeRemove(CommandSource source, float chance) {
        if (ServerFactory.getLegendaryChance() - chance < 0F) {
            source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()), false);
            return 0;
        }

        ServerFactory.removeLegendaryChance(chance);

        source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f",ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "")+ "%")), false);
        return 1;
    }

    private static int executeSet(CommandSource source, float chance) {
        ServerFactory.setLegendaryChance(chance);
        source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f",ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "")+ "%")), false);
        return 1;
    }

    private static int executeReload(CommandSource source) {
        LegendControl.getInstance().loadConfig();
        source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getReload()), false);
        return 1;
    }
}