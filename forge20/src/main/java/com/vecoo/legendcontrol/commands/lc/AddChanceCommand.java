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
        value = "addchance",
        description = "&7/lc addchance <amount>"
)
@Child
public class AddChanceCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource player,
                          @Argument int amount) {
        if (amount >= 0 && amount <= 100) {
            if (LegendarySpawnListener.legendaryChance + amount <= 100) {
                LegendarySpawnListener.legendaryChance += amount;

                player.sendSystemMessage(UtilChatColour.colour(
                        LegendControl.getInstance().getLocale().getMessages().getChangeChanceLegendary()
                                .replace("%chance%", LegendarySpawnListener.legendaryChance + "%")));
            } else {
                player.sendSystemMessage(UtilChatColour.colour(
                        LegendControl.getInstance().getLocale().getMessages().getErrorChance()));
            }
        }
    }
}
