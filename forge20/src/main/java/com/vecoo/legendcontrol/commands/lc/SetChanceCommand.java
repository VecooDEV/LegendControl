package com.vecoo.legendcontrol.commands.lc;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.listener.LegendarySpawnListener;
import net.minecraft.commands.CommandSource;

@Command(
        value = "setchance",
        description = "&7/lc setchance <amount>"
)
@Child
public class SetChanceCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource player,
                          @Argument int amount, String[] args) {
        if (amount >= 0 && amount <= 100) {
            LegendarySpawnListener.legendaryChance = amount;

            player.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                            .replace("%chance%", amount + "%")));
        } else {
            player.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getErrorChance()));
        }
    }
}
