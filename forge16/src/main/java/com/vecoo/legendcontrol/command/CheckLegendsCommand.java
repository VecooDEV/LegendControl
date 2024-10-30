package com.vecoo.legendcontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.LegendServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import java.util.Arrays;

public class CheckLegendsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        for (String command : Arrays.asList("checklegends", "checkleg")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> p.hasPermission(LegendControl.getInstance().getPermission().getPermissionCommand().get("minecraft.command.checklegends")))
                    .executes(e -> execute(e.getSource())));
        }
    }

    private static int execute(CommandSource source) {
        int seconds = (int) ((PixelmonSpawning.legendarySpawner.nextSpawnTime - System.currentTimeMillis()) / 1000 + Utils.timeDoLegend);
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
        source.sendSuccess(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                .replace("%chance%", String.format("%.4f", LegendServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")
                .replace("%time%", time + timeUnit)), false);
    }
}