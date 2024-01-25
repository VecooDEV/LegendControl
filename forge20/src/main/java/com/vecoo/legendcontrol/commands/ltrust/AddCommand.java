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
        value = "add",
        description = "&7/ltrust add <player>"
)
@Child
public class AddCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayer sender,
                          @Completable(PlayerTabCompleter.class)
                          @Argument ServerPlayer target, String[] args) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(sender.getUUID());

        if (sender.getUUID().equals(target.getUUID())) {
            sender.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getCantSelfTrust()));
            return;
        }

        if (senderTrust.getPlayerList().contains(target.getUUID())) {
            sender.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getAlreadyTrusted()));
            return;
        }

        if (senderTrust.getPlayerList().size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
            sender.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getTrustLimit()));
            return;
        }

        senderTrust.addPlayerList(target.getUUID());

        sender.sendSystemMessage(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getPlayerAdded()
                        .replace("%player%", target.getName().getString())));
    }
}