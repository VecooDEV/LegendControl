package com.vecoo.legendcontrol.commands;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.listener.LegendarySpawnListener;
import net.minecraft.commands.CommandSource;

@Command(
        value = "checklegends",
        description = "CheckLegends main command",
        aliases = {
                "checkleg"
        }
)
@Permissible("command.legendcontrol.checkleg")
public class CheckLegendsCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource player, String[] args) {
        player.sendSystemMessage(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getCheckLegendary()
                        .replace("%chance%", LegendarySpawnListener.legendaryChance + "%")
                        .replace("%time1%", PixelmonConfigProxy.getSpawning().getLegendarySpawnTicks() / 20 / 60 + "")
                        .replace("%time2%", PixelmonConfigProxy.getSpawning().getLegendarySpawnTicks() / 20 / 60 * 2 + "")));
    }
}