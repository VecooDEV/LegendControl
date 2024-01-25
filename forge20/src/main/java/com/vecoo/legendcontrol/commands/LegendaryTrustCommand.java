package com.vecoo.legendcontrol.commands;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.vecoo.legendcontrol.commands.ltrust.AddCommand;
import com.vecoo.legendcontrol.commands.ltrust.ListCommand;
import com.vecoo.legendcontrol.commands.ltrust.RemoveAllCommand;
import com.vecoo.legendcontrol.commands.ltrust.RemoveCommand;
import net.minecraft.server.level.ServerPlayer;

@Command(
        value = "legendarytrust",
        description = "&7/ltrust add/remove <player> | list/removeall",
        aliases = {
                    "ltrust"
        }
)
@Permissible("command.legendcontrol.ltrust")
@SubCommands({AddCommand.class, ListCommand.class, RemoveAllCommand.class, RemoveCommand.class})
public class LegendaryTrustCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayer player) {
        player.sendSystemMessage(UtilChatColour.colour("&7/ltrust add/remove <player> | list/removeall"));
    }
}
