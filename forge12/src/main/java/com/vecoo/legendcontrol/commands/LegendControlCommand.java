package com.vecoo.legendcontrol.commands;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.vecoo.legendcontrol.commands.lc.AddCommand;
import com.vecoo.legendcontrol.commands.lc.ReloadCommand;
import com.vecoo.legendcontrol.commands.lc.SetCommand;
import net.minecraft.command.ICommandSender;

@Command(
        value = "legendcontrol",
        description = "LegendControl main command",
        aliases = {
                "lc"
        }
)
@Permissible("legendcontrol.lc")
@SubCommands({AddCommand.class, ReloadCommand.class, SetCommand.class})
public class LegendControlCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSender player) {
    }
}
