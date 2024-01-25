package com.vecoo.legendcontrol.commands.lc;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.commands.CommandSource;

@Command(
        value = "reload",
        description = "Reloads the configs"
)
@Child
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource player, String[] args) {
        LegendControl.getInstance().loadConfig();

        player.sendSystemMessage(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getReloadedConfigs()));
    }
}
