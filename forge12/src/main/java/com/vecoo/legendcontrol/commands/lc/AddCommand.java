package com.vecoo.legendcontrol.commands.lc;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "add",
        description = "§7/lc add <chance>"
)
@Permissible("legendcontrol.lc.add")
@Child
public class AddCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSender player,
                          @Argument int chance, String[] args) {
        if (chance <= 0 || ServerFactory.getLegendaryChance() + chance > 100) {
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getErrorChance())));
            return;
        }

        ServerFactory.addLegendaryChance(chance);

        player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                        .replace("%chance%", ServerFactory.getLegendaryChance() + "%"))));
    }
}