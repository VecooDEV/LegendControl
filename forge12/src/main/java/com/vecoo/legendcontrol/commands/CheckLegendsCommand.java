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
    public void onCommand(@Sender ICommandSender source, String[] args) {
        int seconds = PixelmonConfig.legendarySpawnTicks / 20;
        int minutes = seconds / 20;
        int hours = minutes / 60;

        if (seconds < 60) {
            sendMessage(source, seconds, LegendControl.getInstance().getLocale().getMessages().getSeconds());
        } else if (minutes < 60) {
            sendMessage(source, minutes, LegendControl.getInstance().getLocale().getMessages().getMinutes());
        } else {
            sendMessage(source, hours, LegendControl.getInstance().getLocale().getMessages().getHours());
        }
    }

    private static void sendMessage(ICommandSender source, int time, String timeUnit) {
        source.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&', (LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                .replace("%chance%", ServerFactory.getLegendaryChance() + "%")
                .replace("%time1%", String.valueOf(time))
                .replace("%time2%", time * 2 + timeUnit)))));
    }
}