package com.vecoo.legendcontrol.commands.ltrust;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.providers.PlayerTrust;
import net.minecraft.server.level.ServerPlayer;

@Command(
        value = "remove",
        description = "&7/ltrust remove <player>"
)
@Child
public class RemoveCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayer sender,
                          @Completable(PlayerTabCompleter.class)
                          @Argument ServerPlayer target, String[] args) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(sender.getUUID());

        if (!senderTrust.getPlayerList().contains(target.getUUID())) {
            sender.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getNotPlayerTrust()));
            return;
        }

        senderTrust.removePlayerList(target.getUUID());

        sender.sendSystemMessage(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getPlayerRemoved()
                        .replace("%player%", target.getName().getString())));
    }
}