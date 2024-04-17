package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CheckLegendsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("checkleg").requires(p -> p.hasPermission(2))
                .executes(e -> execute(e.getSource())));
    }

    private static int execute(CommandSource source) {
        if (PixelmonConfigProxy.getSpawning().getLegendarySpawnChance() < 1.0F) {
            source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()), false);
            return 0;
        }

        int ticks = PixelmonConfigProxy.getSpawning().getLegendarySpawnTicks();
        if (ticks / 20 < 60) {
            source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                    .replace("%chance%", ServerFactory.getLegendaryChance() + "%")
                    .replace("%time1%", ticks / 20 + "")
                    .replace("%time2%", ticks / 20 * 2 + LegendControl.getInstance().getLocale().getMessages().getSeconds())), false);
        } else if (ticks / 20 / 60 < 60) {
            source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                    .replace("%chance%", ServerFactory.getLegendaryChance() + "%")
                    .replace("%time1%", ticks / 20 / 60 + "")
                    .replace("%time2%", ticks / 20 / 60 * 2 + LegendControl.getInstance().getLocale().getMessages().getMinutes())), false);
        } else {
            source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                    .replace("%chance%", ServerFactory.getLegendaryChance() + "%")
                    .replace("%time1%", ticks / 20 / 60 / 60 + "")
                    .replace("%time2%", ticks / 20 / 60 / 60 * 2 + LegendControl.getInstance().getLocale().getMessages().getHours())), false);
        }
        return 1;
    }
}