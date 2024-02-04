package com.vecoo.legendcontrol.commands;

import com.envyful.api.forge.chat.UtilChatColour;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.providers.PlayerTrust;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.UsernameCache;

import java.util.Iterator;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("ltrust").requires((p -> Utils.hasPermission(p.getEntity(), "legendcontrol.command.ltrust")))
                .then(Commands.literal("add").then(Commands.argument("player", EntityArgument.players()).
                        executes(e -> executeAdd(e.getSource(), EntityArgument.getPlayer(e, "player")))))
                .then(Commands.literal("remove").then(Commands.argument("player", EntityArgument.players()).
                                executes(e -> executeRemove(e.getSource(), EntityArgument.getPlayer(e, "player"))))
                        .then(Commands.literal("all").executes(e -> executeRemoveAll(e.getSource()))))
                .then(Commands.literal("list").executes(e -> executeList(e.getSource()))));
    }

    private static int executeAdd(CommandSource source, ServerPlayerEntity target) throws CommandSyntaxException {
        UUID sender = source.getPlayerOrException().getUUID();
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(sender);

        if (sender.equals(target.getUUID())) {
            source.sendSuccess(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getCantSelfTrust()), false);
            return 0;
        }

        if (senderTrust.getPlayerList().contains(target.getUUID())) {
            source.sendSuccess(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getAlreadyTrusted()), false);
            return 0;
        }

        if (senderTrust.getPlayerList().size() > LegendControl.getInstance().getConfig().getTrustLimit() && LegendControl.getInstance().getConfig().getTrustLimit() != 0) {
            source.sendSuccess(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getTrustLimit()), false);
            return 0;
        }

        senderTrust.addPlayerList(target.getUUID());

        source.sendSuccess(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getAddTrust()
                        .replace("%player%", target.getName().getString())), false);
        return 1;
    }

    private static int executeRemove(CommandSource source, ServerPlayerEntity target) throws CommandSyntaxException {
        UUID sender = source.getPlayerOrException().getUUID();
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(sender);

        if (senderTrust.getPlayerList().size() == 0) {
            source.sendSuccess(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), false);
            return 0;
        }

        if (!senderTrust.getPlayerList().contains(target.getUUID())) {
            source.sendSuccess(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getNotPlayerTrust()), false);
            return 0;
        }

        senderTrust.removePlayerList(target.getUUID());

        source.sendSuccess(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getRemoveTrust()
                        .replace("%player%", target.getName().getString())), false);
        return 1;
    }

    private static int executeRemoveAll(CommandSource source) throws CommandSyntaxException {
        UUID sender = source.getPlayerOrException().getUUID();
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(sender);

        if (senderTrust.getPlayerList().size() == 0) {
            source.sendSuccess(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), false);
            return 0;
        }

        Iterator<UUID> iterator = senderTrust.getPlayerList().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            iterator.remove();
            senderTrust.removePlayerList(uuid);
        }

        source.sendSuccess(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getRemoveAllTrust()), false);
        return 1;
    }

    private static int executeList(CommandSource source) throws CommandSyntaxException {
        UUID sender = source.getPlayerOrException().getUUID();
        PlayerTrust senderTrust = LegendControl.getInstance().getTrustProvider().getPlayerTrust(sender);
        int size = senderTrust.getPlayerList().size();

        if (size == 0) {
            source.sendSuccess(UtilChatColour.colour(
                    LegendControl.getInstance().getLocale().getMessages().getEmptyTrust()), false);
            return 0;
        }

        source.sendSuccess(UtilChatColour.colour(
                LegendControl.getInstance().getLocale().getMessages().getListTrustTitle()
                        .replace("%amount%", size + "")), false);

        for (UUID uuid : senderTrust.getPlayerList()) {
            String playerName = UsernameCache.getLastKnownUsername(uuid);
            if (playerName != null) {
                source.sendSuccess(UtilChatColour.colour(
                        LegendControl.getInstance().getLocale().getMessages().getListTrust()
                                .replace("%player%", playerName)), false);
            }
        }
        return 1;
    }
}