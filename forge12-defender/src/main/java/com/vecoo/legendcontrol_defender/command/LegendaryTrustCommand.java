package com.vecoo.legendcontrol_defender.command;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.service.LegendControlService;
import com.vecoo.legendcontrol_defender.config.LocaleConfig;
import lombok.val;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LegendaryTrustCommand extends CommandBase {
    @Override
    @Nonnull
    public String getName() {
        return "ltrust";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/ltrust";
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "add", "remove", "list");
        }

        if (args.length == 2) {
            if (!args[0].equals("list") && !args[0].equals("reload")) {
                return getListOfStringsMatchingLastWord(args, LegendControlDefender.getInstance().getServer().getPlayerList().getOnlinePlayerNames());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        try {
            if (!(sender instanceof EntityPlayerMP)) {
                return;
            }

            val player = (EntityPlayerMP) sender;

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
            sender.sendMessage(UtilChat.formatMessage("/ltrust"));
        }
    }

    private static void executeAdd(@Nonnull EntityPlayerMP player, @Nonnull String target) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val targetUUID = UtilPlayer.findUUID(target);

        if (targetUUID == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return;
        }

        if (player.getUniqueID().equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getCantSelfTrust()));
            return;
        }

        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUniqueID());

        if (trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getAlreadyTrusted()));
            return;
        }

        if (LegendControlDefender.getInstance().getServerConfig().getTrustLimit() > 0
            && trustedPlayers.size() >= LegendControlDefender.getInstance().getServerConfig().getTrustLimit()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getTrustLimit()));
            return;
        }

        if (!LegendControlService.addPlayerTrust(player.getUniqueID(), targetUUID)) {
            return;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getAddTrust()
                .replace("%player%", target)));
    }

    private static void executeRemove(@Nonnull EntityPlayerMP player, @Nonnull String target) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val targetUUID = UtilPlayer.findUUID(target);

        if (targetUUID == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return;
        }

        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUniqueID());

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()));
            return;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getNotPlayerTrust()));
            return;
        }

        if (!LegendControlService.removePlayerTrust(player.getUniqueID(), targetUUID)) {
            return;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getRemoveTrust()
                .replace("%player%", target)));
    }

    private static void executeRemoveAll(@Nonnull EntityPlayerMP player) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

        if (LegendControlService.getPlayersTrust(player.getUniqueID()).isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()));
            return;
        }

        if (!LegendControlService.clearPlayersTrust(player.getUniqueID())) {
            return;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getRemoveAllTrust()));
    }

    private static void executeList(@Nonnull EntityPlayerMP player) {
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUniqueID());

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()));
            return;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getListTrust()));

        for (UUID playerUUID : trustedPlayers) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getTrustedPlayers()
                    .replace("%player%", UtilPlayer.getPlayerName(playerUUID))));
        }
    }
}