package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextFormatting;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("lc").requires((p -> p.hasPermission(LegendControl.getInstance().getConfig().getPermissionLegendControl())))
                .then(Commands.literal("add").then(Commands.argument("amount", IntegerArgumentType.integer(0, 100)).
                        executes(e -> executeAdd(e.getSource(), IntegerArgumentType.getInteger(e, "amount")))))
                .then(Commands.literal("set").then(Commands.argument("amount", IntegerArgumentType.integer(0, 100)).
                        executes(e -> executeSet(e.getSource(), IntegerArgumentType.getInteger(e, "amount")))))
                .then(Commands.literal("reload").executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(CommandSource source, int chance) {
        if (LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance() + chance > 100) {
            source.sendSuccess(PixelmonCommandUtils.format(TextFormatting.RED,
                    LegendControl.getInstance().getLocale().getMessages().getErrorChance()), false);
            return 0;
        }

        LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().addChance(chance);

        source.sendSuccess(PixelmonCommandUtils.format(TextFormatting.YELLOW,
                LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                        .replace("%chance%", LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance() + "%")), false);
        return 1;
    }

    private static int executeSet(CommandSource source, int chance) {
        LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().setChance(chance);
        source.sendSuccess(PixelmonCommandUtils.format(TextFormatting.YELLOW,
                LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                        .replace("%chance%", chance + "%")), false);
        return 1;
    }

    private static int executeReload(CommandSource source) {
        LegendControl.getInstance().loadConfig();
        source.sendSuccess(PixelmonCommandUtils.format(TextFormatting.YELLOW,
                LegendControl.getInstance().getLocale().getMessages().getReload()), false);
        return 1;
    }
}