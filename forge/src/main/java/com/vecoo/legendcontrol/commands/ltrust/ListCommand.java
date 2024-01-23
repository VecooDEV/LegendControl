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
import net.minecraftforge.common.UsernameCache;

import java.io.IOException;
import java.util.HashMap;
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
        try {
            HashMap<UUID, List<UUID>> dataMap = UtilsTrust.getDataMap();
            int size = dataMap.get(player.getUniqueID()).size();

            if (size == 0) {
                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                        LegendControl.getInstance().getLocale().getMessages().getEmptyTrust())));
                return;
            }

            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    LegendControl.getInstance().getLocale().getMessages().getListingPlayers()
                            .replace("%amount%", size + ""))));

            for (UUID uuid : dataMap.get(player.getUniqueID())) {
                String nick = UsernameCache.getLastKnownUsername(uuid);
                if (nick != null) {
                    player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                                    LegendControl.getInstance().getLocale().getMessages().getTrustList())
                            .replace("%player%", nick)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}