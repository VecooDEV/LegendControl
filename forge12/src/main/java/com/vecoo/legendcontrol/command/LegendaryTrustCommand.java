package com.vecoo.legendcontrol.command;

import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.player.LegendPlayerFactory;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.UsernameCache;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LegendaryTrustCommand extends CommandBase {

    @Override
    public String getName() {
        return "legendarytrust";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("ltrust");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return LegendControl.getInstance().getLocale().getLegendaryTrustCommand();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return LegendControl.getInstance().getPermission().getPermissionCommand().get("minecraft.command.legendarytrust") == 0 || sender.canUseCommand(2, "gamemode");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "add", "remove", "list");
        }

        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, ExtraLib.getInstance().getServer().getPlayerList().getOnlinePlayerNames());
        }
        return Collections.singletonList("");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            if (!(sender instanceof EntityPlayerMP)) {
                sender.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getOnlyPlayer()));
                return;
            }

            EntityPlayerMP player = (EntityPlayerMP) sender;

            switch (args[0]) {
                case "add": {
                    executeAdd((EntityPlayerMP) sender, args[1]);
                    break;
                }

                case "remove": {
                    if (args[1].equals("all")) {
                        executeRemoveAll(player);
                    } else {
                        executeRemove(player, args[1]);
                    }
                    break;
                }

                case "list": {
                    executeList(player);
                    break;
                }
            }
        } catch (Exception e) {
            sender.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getLegendaryTrustCommand()));
        }
    }

    private void executeAdd(EntityPlayerMP player, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getPlayerNotFound()
                    .replace("%player%", target)));
            return;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> trustedPlayers = LegendPlayerFactory.getPlayersTrust(player.getUniqueID());

        if (player.getUniqueID().equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getCantSelfTrust()));
            return;
        }

        if (trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getAlreadyTrusted()));
            return;
        }

        if (trustedPlayers.size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getTrustLimit()));
            return;
        }

        LegendPlayerFactory.addPlayerTrust(player.getUniqueID(), targetUUID);

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getAddTrust()
                .replace("%player%", target)));
    }

    private void executeRemove(EntityPlayerMP player, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getPlayerNotFound()
                    .replace("%player%", target)));
            return;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> trustedPlayers = LegendPlayerFactory.getPlayersTrust(player.getUniqueID());

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getEmptyTrust()));
            return;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getNotPlayerTrust()));
            return;
        }

        LegendPlayerFactory.removePlayerTrust(player.getUniqueID(), targetUUID);

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getRemoveTrust()
                .replace("%player%", target)));
    }

    private void executeRemoveAll(EntityPlayerMP player) {
        if (LegendPlayerFactory.getPlayersTrust(player.getUniqueID()).isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getEmptyTrust()));
            return;
        }

        LegendPlayerFactory.removePlayersTrust(player.getUniqueID());

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getRemoveAllTrust()));
    }

    private void executeList(EntityPlayerMP player) {
        List<UUID> players = LegendPlayerFactory.getPlayersTrust(player.getUniqueID());

        if (players.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getEmptyTrust()));
            return;
        }

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getListTrust()));

        for (UUID uuid : players) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getListPlayer()
                        .replace("%player%", playerName)));
            }
        }
    }
}