package com.vecoo.legendcontrol.command;

import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermissions;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.LegendServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class CheckLegendsCommand extends CommandBase {

    @Override
    public String getName() {
        return "checklegends";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("checkleg");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/checklegends";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return UtilPermissions.hasPermission(sender, "minecraft.command.checklegends", LegendControl.getInstance().getPermission().getPermissionCommand());
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!UtilPermissions.hasPermission(sender, "minecraft.command.checklegends", LegendControl.getInstance().getPermission().getPermissionCommand())) {
            sender.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getPlayerNotPermission()));
            return;
        }

        int seconds = (int) ((PixelmonSpawning.legendarySpawner.nextSpawnTime - System.currentTimeMillis()) / 1000 + Utils.timeDoLegend);
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

    private static void sendMessage(ICommandSender source, int time, String timeUnit) {
        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getCheckLegendary()
                .replace("%chance%", String.format("%.4f", LegendServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")
                .replace("%time%", time + timeUnit)));
    }
}