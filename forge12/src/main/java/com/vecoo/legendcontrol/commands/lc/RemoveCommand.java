package com.vecoo.legendcontrol.commands.lc;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "remove",
        description = "§7/lc remove <chance>"
)
@Child
public class RemoveCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSender player,
                          @Argument double chance, String[] args) {
        if (chance >= 100 || ServerFactory.getLegendaryChance() - chance < 0) {
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getErrorChance())));
            return;
        }

        ServerFactory.removeLegendaryChance((float) chance);

        player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                        .replace("%chance%", String.format("%.4f",ServerFactory.getLegendaryChance())
                                .replaceAll("\\.?0+$", "")+ "%"))));
    }
}