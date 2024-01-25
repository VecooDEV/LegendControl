package com.vecoo.legendcontrol.commands;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.vecoo.legendcontrol.commands.lc.AddChanceCommand;
import com.vecoo.legendcontrol.commands.lc.ReloadCommand;
import com.vecoo.legendcontrol.commands.lc.SetChanceCommand;
import net.minecraft.server.level.ServerPlayer;

@Command(
        value = "legendcontrol",
        description = "LegendControl main command",
        aliases = {
                "lc"
        }
)
@Permissible("command.legendcontrol.main")
@SubCommands({AddChanceCommand.class, SetChanceCommand.class, ReloadCommand.class})
public class LegendControlCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayer player) {
    }
}