package com.vecoo.legendcontrol.commands;

import com.envyful.api.forge.chat.UtilChatColour;
import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CheckLegendsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("checkleg").requires((p -> Utils.hasPermission(p.getEntity(), "legendcontrol.command.checkleg")))
                .executes(e -> execute(e.getSource())));
    }

    private static int execute(CommandSource source) {
        if (PixelmonConfigProxy.getSpawning().getLegendarySpawnChance() < 1.0F) {
            source.sendSuccess(UtilChatColour.colour(LegendControl.getInstance().getLocale().getMessages().getErrorConfig()), false);
            return 0;
        }

        int ticks = PixelmonConfigProxy.getSpawning().getLegendarySpawnTicks();
        if (ticks / 20 < 60) {
            source.sendSuccess(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                            .replace("%chance%", LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance() + "%")
                            .replace("%time1%", ticks / 20 + "")
                            .replace("%time2%", ticks / 20 * 2 + " seconds")), false);
        } else if (ticks / 20 / 60 < 60) {
            source.sendSuccess(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                            .replace("%chance%", LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance() + "%")
                            .replace("%time1%", ticks / 20 / 60 + "")
                            .replace("%time2%", ticks / 20 / 60 * 2 + " minutes")), false);
        } else {
            source.sendSuccess(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                            .replace("%chance%", LegendControl.getInstance().getLegendaryProvider().getLegendaryChance().getChance() + "%")
                            .replace("%time1%", ticks / 20 / 60 / 60 + "")
                            .replace("%time2%", ticks / 20 / 60 / 60 * 2 + " hours")), false);
        }
        return 1;
    }
}