package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.util.UtilChatColour;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CheckLegendsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("checkleg").requires((p -> Utils.hasPermission(p.getPlayer(), "command.checkleg")))
                .executes(e -> execute(e.getSource())));
    }

    private static int execute(CommandSourceStack source) {
        if (PixelmonConfigProxy.getSpawning().getLegendarySpawnChance() < 1.0F) {
            source.sendSystemMessage(UtilChatColour.colour((LegendControl.getInstance().getLocale().getMessages().getErrorConfig())));
            return 0;
        }

        int ticks = PixelmonConfigProxy.getSpawning().getLegendarySpawnTicks();
        if (ticks / 20 < 60) {
            source.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                            .replace("%chance%", LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance() + "%")
                            .replace("%time1%", ticks / 20 + "")
                            .replace("%time2%", ticks / 20 * 2 + " seconds")));
        } else if (ticks / 20 / 60 < 60) {
            source.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                            .replace("%chance%", LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance() + "%")
                            .replace("%time1%", ticks / 20 / 60 + "")
                            .replace("%time2%", ticks / 20 / 60 * 2 + " minutes")));
        } else {
            source.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                            .replace("%chance%", LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance() + "%")
                            .replace("%time1%", ticks / 20 / 60 / 60 + "")
                            .replace("%time2%", ticks / 20 / 60 / 60 * 2 + " hours")));
        }
        return 1;
    }
}