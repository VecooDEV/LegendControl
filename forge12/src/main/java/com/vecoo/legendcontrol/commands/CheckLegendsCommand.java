package com.vecoo.legendcontrol.commands;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "checklegends",
        description = "CheckLegends main command",
        aliases = {
                "checkleg"
        }
)
@Permissible("legendcontrol.checkleg")
public class CheckLegendsCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSender player, String[] args) {
    int ticks = PixelmonConfig.legendarySpawnTicks;
        if (ticks / 20 < 60) {
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                        .replace("%chance%", ServerFactory.getLegendaryChance() + "%")
                        .replace("%time1%", ticks / 20 + "")
                        .replace("%time2%", ticks / 20 * 2 + LegendControl.getInstance().getLocale().getMessages().getSeconds()))));
    } else if (ticks / 20 / 60 < 60) {
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                        .replace("%chance%", ServerFactory.getLegendaryChance() + "%")
                        .replace("%time1%", ticks / 20 / 60 + "")
                        .replace("%time2%", ticks / 20 / 60 * 2 + LegendControl.getInstance().getLocale().getMessages().getMinutes()))));
    } else {
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                        .replace("%chance%", ServerFactory.getLegendaryChance() + "%")
                        .replace("%time1%", ticks / 20 / 60 / 60 + "")
                        .replace("%time2%", ticks / 20 / 60 / 60 * 2 + LegendControl.getInstance().getLocale().getMessages().getHours()))));
        }
    }
}