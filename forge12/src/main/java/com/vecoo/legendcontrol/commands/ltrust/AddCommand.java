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
        value = "add",
        description = "§7/ltrust add <player>"
)
@Permissible("legendcontrol.ltrust.add")
@Child
public class AddCommand {

    @CommandProcessor
    public void onCommand(@Sender EntityPlayerMP player,
                          @Completable(PlayerTabCompleter.class) @Argument String target, String[] args) {
        if (Utils.hasUUID(target)) {
            UUID targerUUID = Utils.getUUID(target);
            List<UUID> players = PlayerFactory.getPlayers(player.getUniqueID());

            if (player.getUniqueID().equals(targerUUID)) {
                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getCantSelfTrust())));
                return;
            }

            if (players.contains(targerUUID)) {
                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getAlreadyTrusted())));
                return;
            }

            if (players.size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getTrustLimit())));
                return;
            }

            PlayerFactory.addPlayer(player.getUniqueID(), targerUUID);

            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getAddTrust()
                            .replace("%player%", target))));
        } else {
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getPlayerNotFound()
                            .replace("%target%", target))));
        }
    }
}