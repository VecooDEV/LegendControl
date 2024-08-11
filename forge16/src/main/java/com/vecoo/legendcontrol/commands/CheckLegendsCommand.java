package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extrasapi.chat.UtilChat;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import java.util.Arrays;

public class CheckLegendsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        for (String command : Arrays.asList("checklegendary", "checkleg")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> p.hasPermission(LegendControl.getInstance().getPermissions().getPermissions().get("minecraft.command.checklegendary")))
                    .executes(e -> execute(e.getSource())));
        }
    }

    private static int execute(CommandSource source) {
        AbstractSpawner abstrSpawner = PixelmonSpawning.coordinator.getSpawner("legendary");

        if (abstrSpawner == null) {
            abstrSpawner = PixelmonSpawning.legendarySpawner;
        }

        long timeToGo = ((LegendarySpawner) abstrSpawner).nextSpawnTime - System.currentTimeMillis();
        int seconds = (int)Math.ceil(((float)timeToGo / 1000.0F)) + Utils.timeDoLegend;
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
                .replace("%chance%", String.format("%.4f", ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")
                .replace("%time%", time + timeUnit)), false);
    }
}