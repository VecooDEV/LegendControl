package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.listener.LegendarySpawnListener;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("lc").requires((p -> p.hasPermission(2)))
                .then(Commands.literal("add").then(Commands.argument("amount", IntegerArgumentType.integer(0, 100)).
                        executes(e -> executeAdd(e.getSource(), IntegerArgumentType.getInteger(e, "amount")))))
                .then(Commands.literal("set").then(Commands.argument("amount", IntegerArgumentType.integer(0, 100)).
                        executes(e -> executeSet(e.getSource(), IntegerArgumentType.getInteger(e, "amount")))))
                .then(Commands.literal("reload").executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(CommandSource source, int chance) {
        if (LegendarySpawnListener.legendaryChance + chance > 100) {
            PixelmonCommandUtils.sendMessage(source,
                    LegendControl.getInstance().getLocale().getMessages().getErrorChance());
            return 0;
        }

        LegendarySpawnListener.legendaryChance += chance;

        PixelmonCommandUtils.sendMessage(source,
                LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                        .replace("%chance%", LegendarySpawnListener.legendaryChance + "%"));
        return 1;
    }

    private static int executeSet(CommandSource source, int chance) {
        LegendarySpawnListener.legendaryChance = chance;
        PixelmonCommandUtils.sendMessage(source,
                LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                        .replace("%chance%", chance + "%"));
        return 1;
    }

    private static int executeReload(CommandSource source) {
        LegendControl.getInstance().loadConfig();
        PixelmonCommandUtils.sendMessage(source,
                LegendControl.getInstance().getLocale().getMessages().getReload());
        return 1;
    }
}