package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.listener.LegendarySpawnListener;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CheckLegendsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("checkleg")
                .executes(e -> execute(e.getSource())));
    }

    private static int execute(CommandSource source) {
        if (PixelmonConfigProxy.getSpawning().getLegendarySpawnChance() < 1.0F) {
            PixelmonCommandUtils.sendMessage(source, LegendControl.getInstance().getLocale().getMessages().getErrorConfig());
            return 0;
        }

        int ticks = PixelmonConfigProxy.getSpawning().getLegendarySpawnTicks();
        if (ticks / 20 < 60) {
            PixelmonCommandUtils.sendMessage(source,
                    LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                            .replace("%chance%", LegendarySpawnListener.legendaryChance + "%")
                            .replace("%time1%", ticks / 20 + "")
                            .replace("%time2%", ticks / 20 * 2 + " seconds"));
        } else if (ticks / 20 / 60 < 60) {
            PixelmonCommandUtils.sendMessage(source,
                    LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                            .replace("%chance%", LegendarySpawnListener.legendaryChance + "%")
                            .replace("%time1%", ticks / 20 / 60 + "")
                            .replace("%time2%", ticks / 20 / 60 * 2 + " minutes"));
        } else {
            PixelmonCommandUtils.sendMessage(source,
                    LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                            .replace("%chance%", LegendarySpawnListener.legendaryChance + "%")
                            .replace("%time1%", ticks / 20 / 60 / 60 + "")
                            .replace("%time2%", ticks / 20 / 60 / 60 * 2 + " hours"));
        }
        return 1;
    }
}