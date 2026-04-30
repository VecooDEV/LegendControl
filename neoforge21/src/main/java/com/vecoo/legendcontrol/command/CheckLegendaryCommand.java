package com.vecoo.legendcontrol.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.util.PermissionUtil;
import com.vecoo.extralib.util.TextUtil;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.service.LegendControlService;
import com.vecoo.legendcontrol.util.PermissionNodes;
import com.vecoo.legendcontrol.util.Utils;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;

public class CheckLegendaryCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String command : Sets.newHashSet("checklegendary", "checkleg")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.CHECKLEGENDARY_COMMAND))
                    .executes(e -> execute(e.getSource())));
        }
    }

    private static int execute(@NotNull CommandSourceStack source) {
        val localeConfig = LegendControl.getInstance().getLocaleConfig();
        int seconds = (int) ((PixelmonSpawning.legendarySpawner.nextSpawnTime - System.currentTimeMillis()) / 1000 + Utils.TIME_DO_LEGEND);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        if (seconds < 60) {
            sendMessage(source, seconds, localeConfig.getSeconds());
        } else if (minutes < 60) {
            sendMessage(source, minutes, localeConfig.getMinutes());
        } else {
            sendMessage(source, hours, localeConfig.getHours());
        }

        return 1;
    }

    private static void sendMessage(@NotNull CommandSourceStack source, int time, @NotNull String timeUnit) {
        source.sendSystemMessage(TextUtil.formatMessage(LegendControl.getInstance().getLocaleConfig().getCheckLegendary()
                .replace("%chance%", Utils.formatFloat(LegendControlService.getChanceLegend()))
                .replace("%time%", time + timeUnit)));
    }
}