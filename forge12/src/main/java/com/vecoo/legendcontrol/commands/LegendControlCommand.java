package com.vecoo.legendcontrol.commands;

import com.google.common.collect.Lists;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
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

public class LegendControlCommand extends CommandBase {

    @Override
    public String getName() {
        return "legendcontrol";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("lc");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return LegendControl.getInstance().getLocale().getLegendControlCommand();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return LegendControl.getInstance().getPermissions().getPermissionCommand().get("minecraft.command.legendcontrol") == 0;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return Lists.newArrayList("add", "remove", "set", "reload", "blacklist");
        }

        if (args.length == 2) {
            if (!args[0].equals("blacklist")) {
                return Lists.newArrayList("10", "20", "30", "40", "50", "60", "70", "80", "90", "100");
            } else {
                return Lists.newArrayList("add", "remove", "list");
            }
        }

        if (args.length == 3) {
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
            switch (args[0]) {
                case "add": {
                    if (args[1].matches("\\d+")) {
                        executeAdd(sender, Integer.parseInt(args[1]));
                        break;
                    }
                }

                case "remove": {
                    if (args[1].matches("\\d+")) {
                        executeRemove(sender, Integer.parseInt(args[1]));
                        break;
                    }
                }

                case "set": {
                    if (args[1].matches("\\d+")) {
                        executeSet(sender, Integer.parseInt(args[1]));
                        break;
                    }
                }

                case "blacklist": {
                    switch (args[1]) {
                        case "add": {
                            executeBlacklistAdd(sender, args[2]);
                            break;
                        }

                        case "remove": {
                            if (args[2].equals("all")) {
                                executeBlacklistRemoveAll(sender);
                            } else {
                                executeBlacklistRemove(sender, args[2]);
                            }
                            break;
                        }

                        case "list": {
                            executeBlacklist(sender);
                            break;
                        }
                    }
                    break;
                }

                case "reload": {
                    executeReload(sender);
                    break;
                }
            }
        } catch (Exception e) {
            sender.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getLegendControlCommand()));
        }
    }

    private void executeAdd(ICommandSender source, float chance) {
        if (ServerFactory.getLegendaryChance() + chance > 100F) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getErrorChance()));
            return;
        }

        ServerFactory.addLegendaryChance(chance);

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")));
    }

    private void executeRemove(ICommandSender source, float chance) {
        if (ServerFactory.getLegendaryChance() - chance < 0F) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getErrorChance()));
            return;
        }

        ServerFactory.removeLegendaryChance(chance);

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")));
    }

    private void executeSet(ICommandSender source, float chance) {
        ServerFactory.setLegendaryChance(chance);

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", String.format("%.4f", ServerFactory.getLegendaryChance())
                        .replaceAll("\\.?0+$", "") + "%")));
    }

    private void executeBlacklist(ICommandSender source) {
        List<UUID> playersBlacklist = ServerFactory.getPlayersBlacklist();

        if (playersBlacklist.isEmpty()) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getEmptyBlacklist()));
            return;
        }

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getBlacklist()));

        for (UUID uuid : playersBlacklist) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getListPlayer()
                        .replace("%player%", playerName)));
            }
        }
    }

    private void executeBlacklistAdd(ICommandSender source, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getPlayerNotFound()
                    .replace("%target%", target)));
            return;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> playersBlacklist = ServerFactory.getPlayersBlacklist();

        if (playersBlacklist.contains(targetUUID)) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getAlreadyBlacklist()));
            return;
        }

        ServerFactory.addPlayerBlacklist(targetUUID);

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getAddBlacklist()
                .replace("%target%", target)));
    }

    private void executeBlacklistRemove(ICommandSender source, String target) {
        if (!UtilPlayer.hasUUID(target)) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getPlayerNotFound()
                    .replace("%target%", target)));
            return;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        List<UUID> playersBlacklist = ServerFactory.getPlayersBlacklist();

        if (playersBlacklist.isEmpty()) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getEmptyBlacklist()));
            return;
        }

        if (!playersBlacklist.contains(targetUUID)) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getNotPlayerBlacklist()));
            return;
        }

        ServerFactory.removePlayerBlacklist(targetUUID);

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getRemoveBlacklist()
                .replace("%target%", target)));
    }

    private void executeBlacklistRemoveAll(ICommandSender source) {
        if (ServerFactory.getPlayersBlacklist().isEmpty()) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getEmptyTrust()));
            return;
        }

        ServerFactory.removePlayersBlacklist();

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getRemoveAllBlacklist()));
    }

    private void executeReload(ICommandSender source) {
        LegendControl.getInstance().loadConfig();
        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getReload()));
    }
}