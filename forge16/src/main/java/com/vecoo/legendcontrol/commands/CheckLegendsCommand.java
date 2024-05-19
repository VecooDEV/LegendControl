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
        dispatcher.register(Commands.literal("checkleg").requires(p -> p.hasPermission(0))
                .executes(e -> execute(e.getSource())));
    }

    private static int execute(CommandSource source) {
        if (PixelmonConfigProxy.getSpawning().getLegendarySpawnChance() < 1.0F) {
            source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getErrorChance()), false);
            return 0;
        }

        int seconds = PixelmonConfigProxy.getSpawning().getLegendarySpawnTicks() / 20;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        if (seconds < 60) {
            sendMessage(source, seconds, LegendControl.getInstance().getLocale().getMessages().getSeconds());
        } else if (minutes < 60) {
            sendMessage(source, minutes, LegendControl.getInstance().getLocale().getMessages().getMinutes());
        } else {
            sendMessage(source, hours, LegendControl.getInstance().getLocale().getMessages().getHours());
        }
        return 1;
    }

    private static void sendMessage(CommandSource source, int time, String timeUnit) {
        source.sendSuccess(Utils.formatMessage(LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                .replace("%chance%", String.format("%.4f",ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "")+ "%")
                .replace("%time1%", String.valueOf(time))
                .replace("%time2%", time * 2 + timeUnit)), false);
    }
}