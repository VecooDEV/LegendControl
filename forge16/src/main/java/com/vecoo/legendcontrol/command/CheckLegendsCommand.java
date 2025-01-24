package com.vecoo.legendcontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.text.UtilText;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.LegendFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import java.util.ArrayList;

public class CheckLegendsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("checkleg")
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.checkleg"))
                .executes(e -> execute(e.getSource())));
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
                .replace("%chance%", UtilText.getFormattedFloat(LegendFactory.getLegendaryChance()))
                .replace("%time%", time + timeUnit)), false);
        if (UtilPermission.hasPermission(source, "minecraft.command.checkleg.modify")) {
            PixelmonSpawning.legendarySpawner.checkSpawns.checkSpawns(PixelmonSpawning.legendarySpawner, source, new ArrayList<>());
        }
    }
}