package com.vecoo.legendcontrol.commands;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.vecoo.legendcontrol.commands.ltrust.AddCommand;
import com.vecoo.legendcontrol.commands.ltrust.ListCommand;
import com.vecoo.legendcontrol.commands.ltrust.RemoveAllCommand;
import com.vecoo.legendcontrol.commands.ltrust.RemoveCommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "legendarytrust",
        description = "§7/ltrust add/remove <player> | list/removeall",
        aliases = {
                    "ltrust"
        }
)
@SubCommands({AddCommand.class, RemoveCommand.class, RemoveAllCommand.class, ListCommand.class})
public class LegendaryTrustCommand {

        @CommandProcessor
        public void onCommand(@Sender EntityPlayerMP player) {
                player.sendMessage(new TextComponentString("§7/ltrust add/remove <player> | list/removeall"));
        }
}
