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
        value = "set",
        description = "§7/lc set <chance>"
)
@Permissible("legendcontrol.lc.set")
@Child
public class SetCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSender player,
                          @Argument int chance, String[] args) {
        if (chance >= 0 && chance <= 100) {
            ServerFactory.setLegendaryChance(chance);

            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                            .replace("%chance%", chance + "%"))));
        } else {
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getErrorChance())));
        }
    }
}