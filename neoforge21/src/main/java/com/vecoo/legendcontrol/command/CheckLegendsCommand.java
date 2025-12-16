package com.vecoo.legendcontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.service.LegendControlService;
import com.vecoo.legendcontrol.util.PermissionNodes;
import com.vecoo.legendcontrol.util.Utils;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CheckLegendsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("checkleg")
                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.CHECKLEGENDARY_COMMAND))
                .executes(e -> execute(e.getSource())));
    }

    private static int execute(@NotNull CommandSourceStack source) {
        val seconds = (int) ((PixelmonSpawning.legendarySpawner.nextSpawnTime - System.currentTimeMillis()) / 1000 + Utils.TIME_DO_LEGEND);
        val minutes = seconds / 60;
        val hours = minutes / 60;

        if (seconds < 60) {
            sendMessage(source, seconds, LegendControl.getInstance().getLocaleConfig().getSeconds());
        } else if (minutes < 60) {
            sendMessage(source, minutes, LegendControl.getInstance().getLocaleConfig().getMinutes());
        } else {
            sendMessage(source, hours, LegendControl.getInstance().getLocaleConfig().getHours());
        }

        return 1;
    }

    private static void sendMessage(@NotNull CommandSourceStack source, int time, @NotNull String timeUnit) {
        source.sendSystemMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocaleConfig().getCheckLegendary()
                .replace("%chance%", Utils.formatFloat(LegendControlService.getChanceLegend()))
                .replace("%time%", time + timeUnit)));

        if (UtilPermission.hasPermission(source, PermissionNodes.CHECKLEGENDARY_MODIFY_COMMAND)) {
            PixelmonSpawning.legendarySpawner.checkSpawns.checkSpawns(PixelmonSpawning.legendarySpawner, source, new ArrayList<>());
        }
    }
}