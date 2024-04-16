package com.vecoo.legendcontrol.commands.ltrust;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.player.PlayerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.UUID;

@Command(
        value = "remove",
        description = "§7/ltrust remove <player>"
)
@Permissible("legendcontrol.ltrust.remove")
@Child
public class RemoveCommand {

    @CommandProcessor
    public void onCommand(@Sender EntityPlayerMP player,
                          @Completable(PlayerTabCompleter.class) @Argument String target, String[] args) {
        if (Utils.hasUUID(target)) {
            UUID targetUUID = Utils.getUUID(target);
            List<UUID> players = PlayerFactory.getPlayers(player.getUniqueID());

            if (!players.contains(targetUUID)) {
                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getNotPlayerTrust())));
                return;
            }

            PlayerFactory.removePlayer(player.getUniqueID(), targetUUID);

            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getRemoveTrust()
                            .replace("%player%", target))));
        } else {
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                            .replace("%target%", target))));
        }
    }
}