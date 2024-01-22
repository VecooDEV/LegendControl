package com.vecoo.legendcontrol.commands;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.listener.LegendarySpawnListener;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "checklegends",
        description = "CheckLegends main command",
        aliases = {
                "checkleg"
        }
)
public class CheckLegendsCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSender player, String[] args) {
        player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                        .replace("%chance%", LegendarySpawnListener.legendaryChance + "%")
                        .replace("%time1%", PixelmonConfig.legendarySpawnTicks / 20 / 60 + "")
                        .replace("%time2%", PixelmonConfig.legendarySpawnTicks / 20 / 60 * 2 + ""))));
    }
}