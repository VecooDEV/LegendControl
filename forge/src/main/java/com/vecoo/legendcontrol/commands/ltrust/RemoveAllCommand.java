package com.vecoo.legendcontrol.commands.ltrust;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.utils.data.UtilsTrust;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Command(
        value = "removeall",
        description = "§7/ltrust removeall"
)
@Child
public class RemoveAllCommand {

    @CommandProcessor
    public void onCommand(@Sender EntityPlayerMP player, String[] args) {
        try {
            HashMap<UUID, List<UUID>> dataMap = UtilsTrust.getDataMap();

            if (dataMap.get(player.getUniqueID()).size() == 0) {
                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getEmptyTrust())));
                return;
            }

            Iterator<UUID> iterator = dataMap.get(player.getUniqueID()).iterator();
            while (iterator.hasNext()) {
                UUID uuid = iterator.next();
                iterator.remove();
                UtilsTrust.writeToGSON(player.getUniqueID(), uuid, true);
            }

            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getRemovedAll())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
