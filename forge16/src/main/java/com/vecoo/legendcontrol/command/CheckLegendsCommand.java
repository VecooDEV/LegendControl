package com.vecoo.legendcontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.util.PermissionUtil;
import com.vecoo.extralib.util.TextUtil;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.service.LegendControlService;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import javax.annotation.Nonnull;

public class CheckLegendsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("checkleg")
                .requires(p -> PermissionUtil.hasPermission(p, "minecraft.command.checkleg"))
                .executes(e -> execute(e.getSource())));
    }

    private static int execute(@Nonnull CommandSource source) {
        int seconds = (int) ((PixelmonSpawning.legendarySpawner.nextSpawnTime - System.currentTimeMillis()) / 1000 + Utils.TIME_DO_LEGEND);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        if (seconds < 60) {
            sendMessage(source, seconds, LegendControl.getInstance().getLocaleConfig().getSeconds());
        } else if (minutes < 60) {
            sendMessage(source, minutes, LegendControl.getInstance().getLocaleConfig().getMinutes());
        } else {
            sendMessage(source, hours, LegendControl.getInstance().getLocaleConfig().getHours());
        }

        return 1;
    }

    private static void sendMessage(@Nonnull CommandSource source, int time, @Nonnull String timeUnit) {
        source.sendSuccess(TextUtil.formatMessage(LegendControl.getInstance().getLocaleConfig().getCheckLegendary()
                .replace("%chance%", Utils.formatFloat(LegendControlService.getChanceLegend()))
                .replace("%time%", time + timeUnit)), false);
    }
}