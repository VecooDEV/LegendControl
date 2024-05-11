package com.vecoo.legendcontrol.commands.ltrust;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.player.PlayerFactory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.UsernameCache;

import java.util.List;
import java.util.UUID;

@Command(
        value = "list",
        description = "§7/ltrust list"
)
@Child
public class ListCommand {

    @CommandProcessor
    public void onCommand(@Sender EntityPlayerMP player, String[] args) {
        List<UUID> players = PlayerFactory.getPlayers(player.getUniqueID());

        if (players.isEmpty()) {
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust())));
            return;
        }

        player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                LegendControl.getInstance().getLocale().getMessages().getListTrustTitle()
                        .replace("%amount%", players.size() + ""))));

        for (UUID uuid : players) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                                LegendControl.getInstance().getLocale().getMessages().getListTrust())
                        .replace("%player%", playerName)));
            }
        }
    }
}