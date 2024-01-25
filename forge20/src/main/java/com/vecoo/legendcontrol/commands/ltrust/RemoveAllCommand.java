package com.vecoo.legendcontrol.commands.ltrust;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.providers.PlayerTrust;
import net.minecraft.server.level.ServerPlayer;

import java.util.Iterator;
import java.util.UUID;

@Command(
        value = "removeall",
        description = "&7/ltrust removeall"
)
@Child
public class RemoveAllCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayer player, String[] args) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(player.getUUID());

        if (senderTrust.getPlayerList().size() == 0) {
            player.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return;
        }

        Iterator<UUID> iterator = senderTrust.getPlayerList().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            iterator.remove();
            senderTrust.removePlayerList(uuid);
        }

        player.sendSystemMessage(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getRemovedAll()));
    }
}
