package com.vecoo.legendcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.PlayerTrust;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.UsernameCache;

import java.util.Iterator;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("ltrust").requires(p -> p.hasPermission(LegendControl.getInstance().getConfig().getPermissionLegendaryTrust()))
                .then(Commands.literal("add").then(Commands.argument("player", EntityArgument.players()).
                        executes(e -> executeAdd(e.getSource().getPlayerOrException(), EntityArgument.getPlayer(e, "player")))))
                .then(Commands.literal("remove").then(Commands.argument("player", EntityArgument.players()).
                                executes(e -> executeRemove(e.getSource().getPlayerOrException(), EntityArgument.getPlayer(e, "player"))))
                        .then(Commands.literal("all").executes(e -> executeRemoveAll(e.getSource().getPlayerOrException()))))
                .then(Commands.literal("list").executes(e -> executeList(e.getSource().getPlayerOrException()))));
    }

    private static int executeAdd(ServerPlayerEntity sender, ServerPlayerEntity target) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(sender.getUUID());

        if (sender.getUUID().equals(target.getUUID())) {
            sender.sendMessage(PixelmonCommandUtils.format(TextFormatting.RED,
                    LegendControl.getInstance().getLocale().getMessages().getCantSelfTrust()), Util.NIL_UUID);
            return 0;
        }

        if (senderTrust.getPlayerList().contains(target.getUUID())) {
            sender.sendMessage(PixelmonCommandUtils.format(TextFormatting.RED,
                    LegendControl.getInstance().getLocale().getMessages().getAlreadyTrusted()), Util.NIL_UUID);
            return 0;
        }

        if (senderTrust.getPlayerList().size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
            sender.sendMessage(PixelmonCommandUtils.format(TextFormatting.RED,
                    LegendControl.getInstance().getLocale().getMessages().getTrustLimit()), Util.NIL_UUID);
            return 0;
        }

        senderTrust.addPlayerList(target.getUUID());

        sender.sendMessage(PixelmonCommandUtils.format(TextFormatting.YELLOW,
                LegendControl.getInstance().getLocale().getMessages().getAddTrust()
                        .replace("%player%", target.getName().getString())), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemove(ServerPlayerEntity sender, ServerPlayerEntity target) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(sender.getUUID());

        if (senderTrust.getPlayerList().isEmpty()) {
            sender.sendMessage(PixelmonCommandUtils.format(TextFormatting.RED,
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        if (!senderTrust.getPlayerList().contains(target.getUUID())) {
            sender.sendMessage(PixelmonCommandUtils.format(TextFormatting.RED,
                    LegendControl.getInstance().getLocale().getMessages().getNotPlayerTrust()), Util.NIL_UUID);
            return 0;
        }

        senderTrust.removePlayerList(target.getUUID());

        sender.sendMessage(PixelmonCommandUtils.format(TextFormatting.YELLOW,
                LegendControl.getInstance().getLocale().getMessages().getRemoveTrust()
                        .replace("%player%", target.getName().getString())), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemoveAll(ServerPlayerEntity player) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(player.getUUID());

        if (senderTrust.getPlayerList().isEmpty()) {
            player.sendMessage(PixelmonCommandUtils.format(TextFormatting.RED,
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        Iterator<UUID> iterator = senderTrust.getPlayerList().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            iterator.remove();
            senderTrust.removePlayerList(uuid);
        }

        player.sendMessage(PixelmonCommandUtils.format(TextFormatting.YELLOW,
                LegendControl.getInstance().getLocale().getMessages().getRemoveAllTrust()), Util.NIL_UUID);
        return 1;
    }

    private static int executeList(ServerPlayerEntity player) {
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(player.getUUID());
        int size = senderTrust.getPlayerList().size();

        if (size == 0) {
            player.sendMessage(PixelmonCommandUtils.format(TextFormatting.YELLOW,
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(PixelmonCommandUtils.format(TextFormatting.YELLOW,
                LegendControl.getInstance().getLocale().getMessages().getListTrustTitle()
                        .replace("%amount%", size + "")), Util.NIL_UUID);

        for (UUID uuid : senderTrust.getPlayerList()) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                player.sendMessage(PixelmonCommandUtils.format(TextFormatting.YELLOW,
                        LegendControl.getInstance().getLocale().getMessages().getListTrust()
                                .replace("%player%", playerName)), Util.NIL_UUID);
            }
        }
        return 1;
    }
}