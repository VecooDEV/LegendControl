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
import com.vecoo.legendcontrol.utils.data.UtilsTrust;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Command(
        value = "add",
        description = "§7/ltrust add <player>"
)
@Child
public class AddCommand {

    @CommandProcessor
    public void onCommand(@Sender EntityPlayerMP sender,
                          @Completable(PlayerTabCompleter.class)
                          @Argument EntityPlayerMP target, String[] args) {
        try {
            HashMap<UUID, List<UUID>> dataMap = UtilsTrust.getDataMap();
            UUID uuidSender = sender.getUniqueID();
            UUID uuidTarget = target.getUniqueID();

            if (uuidSender.equals(uuidTarget)) {
                sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getCantSelfTrust())));
                return;
            }

            if (dataMap.get(uuidSender).contains(uuidTarget)) {
                sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getAlreadyTrusted())));
                return;
            }

            if (dataMap.get(uuidSender).size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
                sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getTrustLimit())));
                return;
            }

            UtilsTrust.writeToGSON(uuidSender, uuidTarget, false);

            sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getPlayerAdded()
                            .replace("%player%", target.getName()))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}