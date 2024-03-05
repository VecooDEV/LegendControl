package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.vecoo.extraapi.chat.UtilChat;
import com.vecoo.extraapi.player.UtilPlayer;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.providers.PlayerTrust;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.UsernameCache;

import java.util.Iterator;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ltrust").requires((p -> UtilPlayer.hasPermission(p.getPlayer(), "command.ltrust")))
                .then(Commands.literal("add").then(Commands.argument("player", EntityArgument.players()).
                        executes(e -> executeAdd(e.getSource().getPlayerOrException(), EntityArgument.getPlayer(e, "player")))))
                .then(Commands.literal("remove").then(Commands.argument("player", EntityArgument.players()).
                                executes(e -> executeRemove(e.getSource().getPlayerOrException(), EntityArgument.getPlayer(e, "player"))))
                        .then(Commands.literal("all").executes(e -> executeRemoveAll(e.getSource().getPlayerOrException()))))
                .then(Commands.literal("list").executes(e -> executeList(e.getSource().getPlayerOrException()))));
    }

    private static int executeAdd(ServerPlayer sender, ServerPlayer target) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(sender.getUUID());

        if (sender.getUUID().equals(target.getUUID())) {
            sender.sendSystemMessage(UtilChat.formatMessage(
                    LegendControl.getInstance().getLocale().getMessages().getCantSelfTrust()));
            return 0;
        }

        if (senderTrust.getPlayerList().contains(target.getUUID())) {
            sender.sendSystemMessage(UtilChat.formatMessage(
                    LegendControl.getInstance().getLocale().getMessages().getAlreadyTrusted()));
            return 0;
        }

        if (senderTrust.getPlayerList().size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
            sender.sendSystemMessage(UtilChat.formatMessage(
                    LegendControl.getInstance().getLocale().getMessages().getTrustLimit()));
            return 0;
        }

        senderTrust.addPlayerList(target.getUUID());

        sender.sendSystemMessage(UtilChat.formatMessage(
                LegendControl.getInstance().getLocale().getMessages().getAddTrust()
                        .replace("%player%", target.getName().getString())));
        return 1;
    }

    private static int executeRemove(ServerPlayer sender, ServerPlayer target) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(sender.getUUID());

        if (senderTrust.getPlayerList().isEmpty()) {
            sender.sendSystemMessage(UtilChat.formatMessage(
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return 0;
        }

        if (!senderTrust.getPlayerList().contains(target.getUUID())) {
            sender.sendSystemMessage(UtilChat.formatMessage(
                    LegendControl.getInstance().getLocale().getMessages().getNotPlayerTrust()));
            return 0;
        }

        senderTrust.removePlayerList(target.getUUID());

        sender.sendSystemMessage(UtilChat.formatMessage(
                LegendControl.getInstance().getLocale().getMessages().getRemoveTrust()
                        .replace("%player%", target.getName().getString())));
        return 1;
    }

    private static int executeRemoveAll(ServerPlayer player) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(player.getUUID());

        if (senderTrust.getPlayerList().isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return 0;
        }

        Iterator<UUID> iterator = senderTrust.getPlayerList().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            iterator.remove();
            senderTrust.removePlayerList(uuid);
        }

        player.sendSystemMessage(UtilChat.formatMessage(
                LegendControl.getInstance().getLocale().getMessages().getRemoveAllTrust()));
        return 1;
    }

    private static int executeList(ServerPlayer player) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(player.getUUID());
        int size = senderTrust.getPlayerList().size();

        if (size == 0) {
            player.sendSystemMessage(UtilChat.formatMessage(
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(
                LegendControl.getInstance().getLocale().getMessages().getListTrustTitle()
                        .replace("%amount%", size + "")));

        for (UUID uuid : senderTrust.getPlayerList()) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                player.sendSystemMessage(UtilChat.formatMessage(
                        LegendControl.getInstance().getLocale().getMessages().getListTrust()
                                .replace("%player%", playerName)));
            }
        }
        return 1;
    }
}