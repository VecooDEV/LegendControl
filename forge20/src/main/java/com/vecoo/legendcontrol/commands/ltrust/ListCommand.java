package com.vecoo.legendcontrol.commands.ltrust;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.providers.PlayerTrust;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.UsernameCache;

import java.util.UUID;

@Command(
        value = "list",
        description = "&7/ltrust list"
)
@Child
public class ListCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayer player, String[] args) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(player.getUUID());
        int size = senderTrust.getPlayerList().size();

        if (size == 0) {
            player.sendSystemMessage(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return;
        }

        player.sendSystemMessage(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getListingPlayers()
                        .replace("%amount%", size + "")));

        for (UUID uuid : senderTrust.getPlayerList()) {
            String nick = UsernameCache.getLastKnownUsername(uuid);
            if (nick != null) {
                player.sendSystemMessage(UtilChatColour.colour(
                        LegendControl.getInstance().getLocale().getMessages().getTrustList()
                                .replace("%player%", nick)));
            }
        }
    }
}