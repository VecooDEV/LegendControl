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

import static com.vecoo.legendcontrol.utils.data.UtilsTrust.getDataMap;

@Command(
        value = "remove",
        description = "§7/ltrust remove <player>"
)
@Child
public class RemoveCommand {

    @CommandProcessor
    public void onCommand(@Sender EntityPlayerMP sender,
                          @Completable(PlayerTabCompleter.class)
                          @Argument EntityPlayerMP target, String[] args) {
        try {
            HashMap<UUID, List<UUID>> dataMap = getDataMap();
            UUID uuidSender = sender.getUniqueID();
            UUID uuidTarget = target.getUniqueID();

            if (!dataMap.get(uuidSender).contains(uuidTarget)) {
                sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getNotPlayerTrust())));
                return;
            }

            UtilsTrust.writeToGSON(uuidSender, uuidTarget, true);

            sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getPlayerRemoved()
                            .replace("%player%", target.getName()))));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
