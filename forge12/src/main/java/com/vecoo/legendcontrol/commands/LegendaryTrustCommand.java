package com.vecoo.legendcontrol.commands;

import com.google.common.collect.Lists;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.player.PlayerFactory;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.UsernameCache;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
        return LegendControl.getInstance().getPermissions().getPermissionCommand().get("minecraft.command.legendarytrust") == 0;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return Lists.newArrayList("add", "remove", "list");
        }

        if (args.length == 2) {
            List<String> players = new ArrayList<>();
            for (EntityPlayerMP player : ExtraLib.getInstance().getServer().getPlayerList().getPlayers()) {
                players.add(player.getName());
            }
            return players;
        }
        return null;
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
                    .replace("%target%", target)));
            return;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> trustedPlayers = PlayerFactory.getPlayersTrust(player.getUniqueID());

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

        PlayerFactory.addPlayerTrust(player.getUniqueID(), targetUUID);

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getAddTrust()
                .replace("%target%", target)));
    }

    private void executeRemove(EntityPlayerMP player, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getPlayerNotFound()
                    .replace("%target%", target)));
            return;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> trustedPlayers = PlayerFactory.getPlayersTrust(player.getUniqueID());

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getEmptyTrust()));
            return;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getNotPlayerTrust()));
            return;
        }

        PlayerFactory.removePlayerTrust(player.getUniqueID(), targetUUID);

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getRemoveTrust()
                .replace("%target%", target)));
    }

    private void executeRemoveAll(EntityPlayerMP player) {
        if (PlayerFactory.getPlayersTrust(player.getUniqueID()).isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getEmptyTrust()));
            return;
        }

        PlayerFactory.removePlayersTrust(player.getUniqueID());

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getRemoveAllTrust()));
    }

    private void executeList(EntityPlayerMP player) {
        List<UUID> players = PlayerFactory.getPlayersTrust(player.getUniqueID());

        if (players.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getEmptyTrust()));
            return;
        }

        player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getListTrust()
                .replace("%amount%", players.size() + "")));

        for (UUID uuid : players) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                player.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getListPlayer()
                        .replace("%player%", playerName)));
            }
        }
    }
}