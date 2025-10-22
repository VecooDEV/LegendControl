package com.vecoo.legendcontrol.command;

import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.text.UtilText;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;

public class CheckLegendsCommand extends CommandBase {
    @Override
    @Nonnull
    public String getName() {
        return "checkleg";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/checkleg";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        int seconds = (int) ((PixelmonSpawning.legendarySpawner.nextSpawnTime - System.currentTimeMillis()) / 1000 + Utils.TIME_DO_LEGEND);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        if (seconds < 60) {
            sendMessage(sender, seconds, LegendControl.getInstance().getLocale().getSeconds());
        } else if (minutes < 60) {
            sendMessage(sender, minutes, LegendControl.getInstance().getLocale().getMinutes());
        } else {
            sendMessage(sender, hours, LegendControl.getInstance().getLocale().getHours());
        }
    }

    private static void sendMessage(@Nonnull ICommandSender source, int time, @Nonnull String timeUnit) {
        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getCheckLegendary()
                .replace("%chance%", UtilText.getFormattedFloat(LegendControlFactory.ServerProvider.getChanceLegend()))
                .replace("%time%", time + timeUnit)));
    }
}